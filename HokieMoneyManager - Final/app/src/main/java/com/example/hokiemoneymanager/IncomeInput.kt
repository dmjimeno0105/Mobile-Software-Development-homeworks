package com.example.hokiemoneymanager

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.hokiemoneymanager.databinding.FragmentIncomeInputBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class IncomeInput : Fragment() {
    private val db = Firebase.firestore
    private var binding: FragmentIncomeInputBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomeInputBinding.inflate(inflater, container, false)

        binding?.submitButton?.setOnClickListener {
            val categoryInput = binding?.categoryInput?.text.toString() // Convert to String
            val categoryAmountInput = binding?.categoryAmountInput?.text.toString().trim()

            val incomeAmount = categoryAmountInput.toDoubleOrNull() ?: run {
                0.0
            }

            val user = hashMapOf(
                "name" to categoryInput,
                "amount" to incomeAmount
            )

            db.collection("income")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            binding?.root?.findNavController()?.navigate(R.id.action_incomeInput_to_income)
        }

        return binding?.root
    }
}