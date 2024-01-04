package com.example.hokiemoneymanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userImageView: ImageView
    private lateinit var changeNameButton: Button
    private lateinit var changeImageButton: Button
    private lateinit var changeEmailButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNameTextView = view.findViewById(R.id.userName)
        userEmailTextView = view.findViewById(R.id.userEmail)
        userImageView = view.findViewById(R.id.userImageView)
        changeNameButton = view.findViewById(R.id.changeNameButton)
        changeImageButton = view.findViewById(R.id.changeImageButton)
        changeEmailButton = view.findViewById(R.id.changeEmailButton)

        // Set up button click listeners
        changeNameButton.setOnClickListener {
            // Logic to change user's name
        }

        changeImageButton.setOnClickListener {
            // Logic to change user's image
        }

        changeEmailButton.setOnClickListener {
            // Logic to change user's email
        }
    }
}