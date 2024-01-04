package com.example.hokiemoneymanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hokiemoneymanager.databinding.FragmentSettingsBinding
import org.json.JSONObject
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class Settings : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val model: ViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->        // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)        // Apply the adapter to the spinner
            binding.spinnerCurrency.adapter = adapter
//            binding.spinnerCurrency.setSelection(adapter.getPosition(model.selectedItem.value))
            model.spinnerPosition.value?.let {
                binding.spinnerCurrency.setSelection(it)
            } ?: run {
                binding.spinnerCurrency.setSelection(0) // Setting default selection to the first item
            }

        }

        binding.spinnerCurrency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    val value = getValueFromJson(selectedItem)
                    model.setCountryName(selectedItem)

                    model.setCurrencySymbol(value)
                    model.setSpinnerPosition(position)

                    model.countrySymbol.observe(viewLifecycleOwner) { countrySymbol ->
                        model.setExchangeRate(value)
                        countrySymbol?.let {
                            Log.d("Settings.kt countrySymbol", model.countrySymbol.value.toString())
                            Log.d("Settings.kt exchangeRate", model.exchangeRate.value.toString())
                        }
                    }
                    Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_settings, container, false)
        return binding.root
    }

    private fun getValueFromJson(selectedItem: String): String {
        val jsonString = loadJSONFromAsset()
        val jsonObject = JSONObject(jsonString)
        return jsonObject.optString(selectedItem, "Not found")
    }

    private fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val inputStream = context?.assets?.open("Countries.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json ?: ""
    }
}