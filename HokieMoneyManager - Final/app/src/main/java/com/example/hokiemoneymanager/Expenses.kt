package com.example.hokiemoneymanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hokiemoneymanager.databinding.FragmentExpensesBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal

/**
 * A simple [Fragment] subclass.
 */
class Expenses : Fragment() {
    private lateinit var binding: FragmentExpensesBinding
    private lateinit var adapter: ExpensesAdapter
    private val model: ViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentExpensesBinding.inflate(layoutInflater)
        adapter = ExpensesAdapter()
        binding.expenseRecyclerView.adapter = adapter
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_expenses_to_expenseInput2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_expenses, container, false)

        return binding.root
    }

    inner class ExpensesAdapter : RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder>() {
        private var expensesList = mutableListOf<ExpensesItem>()

        init {
            fetchDataFromFirestore()
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun fetchDataFromFirestore() {
            val db = FirebaseFirestore.getInstance()
            db.collection("expenses")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val expenseItem = document.toObject(ExpensesItem::class.java)

                        expensesList.add(expenseItem)
                    }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("ExpensesAdapter", "Error getting documents: ", exception)
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_card_view, parent, false)
            return ExpensesViewHolder(v)
        }

        override fun getItemCount(): Int {
            return expensesList.size
        }

        override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) {
            holder.view?.findViewById<TextView>(R.id.source)?.text = expensesList[position].name
            model.countrySymbol.observe(viewLifecycleOwner) { countrySymbol ->
                model.exchangeRate.observe(viewLifecycleOwner) { exchangeRate ->
                    var inputCurrency: BigDecimal
                    if (countrySymbol == "USD")
                        inputCurrency = expensesList[position].amount.toBigDecimal()
                    else
                        inputCurrency = expensesList[position].amount.toBigDecimal() * exchangeRate
                    val value = model.countrySymbol.value?.let { getValueFromJson(it) }.toString()
                    "$value${model.formatCurrency(inputCurrency)}".also {
                        holder.view?.findViewById<TextView>(R.id.amount)?.text = it
                    }
                }
            }
        }

        inner class ExpensesViewHolder(val view: View?) : RecyclerView.ViewHolder(view!!),
            View.OnClickListener {
            override fun onClick(v: View?) {
            }
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
}