package edu.vt.cs3714.retrofitrecyclerviewguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.vt.cs3714.retrofitrecyclerviewguide.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}