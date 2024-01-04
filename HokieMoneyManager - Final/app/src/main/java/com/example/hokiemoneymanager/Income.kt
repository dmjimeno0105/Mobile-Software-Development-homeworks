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
import com.example.hokiemoneymanager.databinding.FragmentIncomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal


class Income : Fragment() {
    private lateinit var binding: FragmentIncomeBinding
    private lateinit var adapter: IncomeAdapter
    private val model: ViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentIncomeBinding.inflate(layoutInflater)
        adapter = IncomeAdapter()
        binding.income.adapter = adapter
        binding.income.layoutManager = LinearLayoutManager(binding.root.context)
        binding.IncomePlus.setOnClickListener {
            findNavController().navigate(R.id.action_income_to_incomeInput)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_income, container, false)
        return binding.root
    }

    inner class IncomeAdapter : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {
        private var incomeList = mutableListOf<IncomeItem>()

        init {
            fetchDataFromFirestore()
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun fetchDataFromFirestore() {
            val db = FirebaseFirestore.getInstance()
            db.collection("income")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val incomeItem = document.toObject(IncomeItem::class.java)
                        incomeList.add(incomeItem)
                    }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("IncomeAdapter", "Error getting documents: ", exception)
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_card_view, parent, false)
            return IncomeViewHolder(v)
        }

        override fun getItemCount(): Int {
            return incomeList.size
        }

        override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
            holder.view?.findViewById<TextView>(R.id.source)?.text = incomeList[position].name
            model.countrySymbol.observe(viewLifecycleOwner) { countrySymbol ->
                model.exchangeRate.observe(viewLifecycleOwner) { exchangeRate ->
                    val value = model.countrySymbol.value?.let { getValueFromJson(it) }.toString()
                    var inputCurrency: BigDecimal
                    if (countrySymbol == "USD")
                        inputCurrency = incomeList[position].amount.toBigDecimal()
                    else
                        inputCurrency = incomeList[position].amount.toBigDecimal() * exchangeRate
                    "$value${model.formatCurrency(inputCurrency)}".also {
                        holder.view?.findViewById<TextView>(R.id.amount)?.text = it
                    }
                }
            }
        }

        inner class IncomeViewHolder(val view: View?) : RecyclerView.ViewHolder(view!!),
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