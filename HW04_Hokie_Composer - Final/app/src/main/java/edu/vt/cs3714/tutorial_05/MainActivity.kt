package edu.vt.cs3714.tutorial_05

import FragmentPagerAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import edu.vt.cs3714.tutorial_05.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), EditingFragment.ViewPagerController {
    private var binding: ActivityMainBinding? = null
    private lateinit var viewPager: ViewPager2

    // Go here to see posts: https://webhook.site/#!/51766d6e-b08d-4036-8676-4adc9c7fa000/c574d6be-fb2a-4869-87ec-3177ccf6d36f/1

    companion object {
        const val TAG       = "HW04 Hokie Composer"
        const val USER_ID  =  "dmjimeno0105"
        const val URL       = "https://webhook.site"
        const val ROUTE     = "/51766d6e-b08d-4036-8676-4adc9c7fa000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Handle intent to navigate to the correct fragment
        if (intent?.getStringExtra("navigateTo") == "PlayingFragment") {
            // Code to display PlayingFragment
            viewPager.currentItem = 1
        }

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = binding?.viewPager!!
        val pagerAdapter: FragmentStateAdapter = FragmentPagerAdapter(this)
        viewPager.adapter = pagerAdapter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Make sure to call setIntent so that getIntent() will return the latest intent that was used to start this Activity
        setIntent(intent)

        // Check if we need to navigate to a particular fragment
        if (intent.getStringExtra("navigateTo") == "PlayingFragment") {
            // Code to navigate to PlayingFragment
            viewPager.currentItem = 1
        }
    }


    override fun navigateToPage(pageIndex: Int) {
        viewPager.currentItem = pageIndex
    }
}