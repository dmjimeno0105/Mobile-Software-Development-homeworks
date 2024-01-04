package com.example.hokiemoneymanager

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.hokiemoneymanager.databinding.FragmentExpenseInputBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class ExpensesInput : Fragment() {
    private var binding: FragmentExpenseInputBinding? = null
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseInputBinding.inflate(inflater, container, false)

        //val submit: Button = R.id.expense_submit_button
        binding?.expenseSubmitButton?.setOnClickListener{
            val categoryInput = binding?.expenseNameInput?.text.toString() // Convert to String
            val categoryAmountInput = binding?.expenseAmountInput?.text.toString().trim()

            val incomeAmount = categoryAmountInput.toDoubleOrNull() ?: run {
                0.0
            }

            val user = hashMapOf(
                "name" to categoryInput,
                "amount" to incomeAmount
            )

            db.collection("expenses")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
            binding?.root?.findNavController()?.navigate(R.id.action_expenseInput2_to_expenses)
        }
        return binding?.root
    }


}