package com.example.hokiemoneymanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hokiemoneymanager.databinding.FragmentLandingPageBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal

/**
 * LandingPage fragment class
 */
class LandingPage : Fragment() {
    private lateinit var binding: FragmentLandingPageBinding
    private var drawerLayout: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    private val model: ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingPageBinding.inflate(layoutInflater)

        val toolbar: MaterialToolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout?.addDrawerListener(toggle!!)
        toggle!!.syncState()

        fetchDataFromFirestoreIncome {
            model.setIncomeTotal(it)
        }

        fetchDataFromFirestoreExpenses {
            model.setExpensesTotal(it)
        }

        model.incomeTotal.observe(viewLifecycleOwner) { income ->
            var incomeTotal = income.toBigDecimal()
            val value = model.countrySymbol.value?.let { getValueFromJson(it) }.toString()
            model.countrySymbol.observe(viewLifecycleOwner) { countrySymbol ->
                model.exchangeRate.observe(viewLifecycleOwner) { exchangeRate ->
                    var inputCurrency: BigDecimal
                    if (countrySymbol == "USD")
                        inputCurrency = incomeTotal
                    else
                        inputCurrency = incomeTotal * exchangeRate
                    "$value${model.formatCurrency(inputCurrency)}".also {
                        binding.incomeValue.text = it
                    }
                }
            }

            model.expensesTotal.observe(viewLifecycleOwner) { expenses ->
                var expensesTotal = expenses.toBigDecimal()
                model.setBalance(incomeTotal - expensesTotal)
            }
        }

        model.expensesTotal.observe(viewLifecycleOwner) { expenses ->
            var expensesTotal = expenses.toBigDecimal()
            val value = model.countrySymbol.value?.let { getValueFromJson(it) }.toString()
            model.countrySymbol.observe(viewLifecycleOwner) { countrySymbol ->
                model.exchangeRate.observe(viewLifecycleOwner) { exchangeRate ->
                    var inputCurrency: BigDecimal
                    if (countrySymbol == "USD")
                        inputCurrency = expensesTotal
                    else
                        inputCurrency = expensesTotal * exchangeRate
                    "$value${model.formatCurrency(inputCurrency)}".also {
                        binding.expensesValue.text = it
                    }
                }
            }

            model.incomeTotal.observe(viewLifecycleOwner) { income ->
                var incomeTotal = income.toBigDecimal()
                model.setBalance(incomeTotal - expensesTotal)
            }
        }

        model.balance.observe(viewLifecycleOwner) { balance ->
            val value = model.countrySymbol.value?.let { getValueFromJson(it) }.toString()
            model.countrySymbol.observe(viewLifecycleOwner) { countrySymbol ->
                model.exchangeRate.observe(viewLifecycleOwner) { exchangeRate ->
                    var inputCurrency = 0.0.toBigDecimal()
                    if (countrySymbol == "USD")
                        inputCurrency = Math.abs(balance.toDouble()).toBigDecimal()
                    else
                        inputCurrency = Math.abs(balance.toDouble()).toBigDecimal() * exchangeRate
                    "$value${model.formatCurrency(inputCurrency)}".also {
                        binding.balanceValue.text = it
                        if (balance > 0.toBigDecimal()) {
                            binding.balanceValue.setBackgroundColor(resources.getColor(R.color.green))
                        } else {
                            binding.balanceValue.setBackgroundColor(resources.getColor(R.color.red))
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navigationView: NavigationView = binding.navView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.landingPage)
                }

                R.id.nav_settings -> {
                    findNavController().navigate(R.id.action_landingPage_to_settings)
                }

                R.id.nav_Expenses -> {
                    findNavController().navigate(R.id.action_landingPage_to_expenses)
                }

                R.id.nav_Income -> {
                    findNavController().navigate(R.id.action_landingPage_to_income)
                }
            }

            drawerLayout?.closeDrawer(GravityCompat.START)
            true
        }

        binding.incomeValue.setOnClickListener {
            findNavController().navigate(R.id.action_landingPage_to_income)

        }

        binding.expensesValue.setOnClickListener {
            findNavController().navigate(R.id.action_landingPage_to_expenses)
        }
    }

    private fun getValueFromJson(selectedItem: String): String {
        val jsonString = loadJSONFromAsset()
        val jsonObject = JSONObject(jsonString)
        return jsonObject.optString(selectedItem, "Not found")
    }

    private fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val inputStream = context?.assets?.open("Symbols.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json ?: ""
    }

    private fun fetchDataFromFirestoreIncome(callback: (Double) -> Unit) {
        var incomeTotal = 0.0
        val db = FirebaseFirestore.getInstance()
        db.collection("income")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val amount = document.getDouble("amount") ?: 0.0
                    incomeTotal += amount
                }
                callback(incomeTotal)
            }
            .addOnFailureListener {
                callback(0.0)
            }
    }

    private fun fetchDataFromFirestoreExpenses(callback: (Double) -> Unit) {
        var expensesTotal = 0.0
        val db = FirebaseFirestore.getInstance()
        db.collection("expenses")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val amount = document.getDouble("amount") ?: 0.0
                    expensesTotal += amount
                }
                callback(expensesTotal)
            }
            .addOnFailureListener {
                callback(0.0)
            }
    }
}