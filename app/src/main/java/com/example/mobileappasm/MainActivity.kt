package com.example.mobileappasm

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mobileappasm.R
import com.example.mobileappasm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userNavController: NavController
    private lateinit var adminNavController: NavController
    private lateinit var userAppBarConfiguration: AppBarConfiguration
    private lateinit var adminAppBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        // Set up user nav controller and app bar configuration
        val userNavHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        userNavController = userNavHostFragment.navController
        userAppBarConfiguration = AppBarConfiguration(setOf(R.id.cusLoginPage, R.id.cusMainPage), drawerLayout)

        // Set up admin nav controller and app bar configuration
        val adminNavHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        adminNavController = adminNavHostFragment.navController
        adminAppBarConfiguration = AppBarConfiguration(setOf(R.id.adminLoginPage, R.id.adminProfile), drawerLayout)

        val sharedPref = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val isAdmin = sharedPref.getBoolean("is_admin", false)
        val isUser = sharedPref.getBoolean("is_user", false)

        // Set up the nav controller and app bar configuration based on user role
        if (isAdmin) {
            // Set up admin navigation drawer
            binding.navViewUser.visibility = View.GONE
            binding.navViewAdmin.visibility = View.VISIBLE
            NavigationUI.setupWithNavController(binding.navViewAdmin, adminNavController)
            NavigationUI.setupActionBarWithNavController(this, adminNavController, adminAppBarConfiguration)
        } else if (isUser){
            // Set up user navigation drawer
            binding.navViewAdmin.visibility = View.GONE
            binding.navViewUser.visibility = View.VISIBLE
            NavigationUI.setupWithNavController(binding.navViewUser, userNavController)
            NavigationUI.setupActionBarWithNavController(this, userNavController, userAppBarConfiguration)
        }
    }

//    private fun isUserLoggedInAsAdmin(): Boolean {
//        // Implement your own logic to determine if user is logged in as admin
//        val sharedPref = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
//        val isAdmin = sharedPref.getBoolean("is_admin", false)
//
//        if (isAdmin) {
//            // Admin login
//            return true
//        }
//        return false
//    }

    override fun onSupportNavigateUp(): Boolean {
        val sharedPref = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val isAdmin = sharedPref.getBoolean("is_admin", false)

        // Handle up navigation based on user role
        return if (isAdmin) {
            NavigationUI.navigateUp(adminNavController, adminAppBarConfiguration)
        } else {
            NavigationUI.navigateUp(userNavController, userAppBarConfiguration)
        } || super.onSupportNavigateUp()
    }
}
