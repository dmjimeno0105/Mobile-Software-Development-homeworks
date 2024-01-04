package com.example.hokiemoneymanager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment

/**
 * MainActivity
 *
 * @author Nathan Damte
 * @author Dominic Jimeno
 * @author Dhruv Varshney
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    companion object {
        const val TAG       = "Hokie Money Manager"
        const val USER_ID  =  "Group 19"
        const val URL       = "https://url.com"
        const val ROUTE     = "/this_page"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate called")

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        navigationView.menu.findItem(R.id.nav_home).setOnMenuItemClickListener {
            Log.d("MainActivity", "Home menu item clicked")
            navController.navigate(R.id.landingPage)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

// Repeat for other menu items...



        navigationView.setNavigationItemSelectedListener { menuItem ->
            Log.d("MainActivity", "Menu item clicked: ${menuItem.itemId}")
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.landingPage)
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.action_landingPage_to_settings)
                }
                // Handle other items...
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

//        // Adding Toolbar and Hamburger Icon
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        val toggle = ActionBarDrawerToggle(
//            this, drawerLayout, toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
    }
}
