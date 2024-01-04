package com.example.hw02_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hw02_stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}