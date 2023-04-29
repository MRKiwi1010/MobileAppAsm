package com.example.mobileappasm

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private lateinit var adminNavView: NavigationView
    private lateinit var userNavView: NavigationView
    private var adminNavController: NavController? = null
    private var userNavController: NavController? = null
    private var adminAppBarConfig: AppBarConfiguration? = null
    private var userAppBarConfig: AppBarConfiguration? = null
    private var isAdmin = false // flag to indicate whether the user is an admin or not

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)


        // Admin navigation drawer
        adminNavView = findViewById(R.id.nav_view_admin)
        adminNavController = Navigation.findNavController(this, R.id.myNavHostFragment)
        adminAppBarConfig = AppBarConfiguration.Builder(adminNavController!!.graph)
            .setDrawerLayout(drawerLayout)
            .build()
        adminNavView?.let {
            NavigationUI.setupWithNavController(it, adminNavController!!)
        }
        NavigationUI.setupActionBarWithNavController(
            this, adminNavController!!,
            adminAppBarConfig!!
        )

        // User navigation drawer
        userNavView = findViewById(R.id.nav_view_user)
        userNavController = Navigation.findNavController(this, R.id.myNavHostFragment)
        userAppBarConfig = AppBarConfiguration.Builder(userNavController!!.graph)
            .setDrawerLayout(drawerLayout)
            .build()
        userNavView?.let {
            NavigationUI.setupWithNavController(it, userNavController!!)
        }
        NavigationUI.setupActionBarWithNavController(this, userNavController!!, userAppBarConfig!!)

        // Hide both navigation drawers initially
        adminNavView.setVisibility(View.GONE)
        userNavView.setVisibility(View.GONE)

        /// Retrieve the isAdmin flag from shared preferences or database
        val sharedPref = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        isAdmin = sharedPref.getBoolean("is_admin", false)

        if (isAdmin) {
            adminNavView.setVisibility(View.GONE)
            NavigationUI.setupActionBarWithNavController(
                this,
                adminNavController!!, adminAppBarConfig!!
            )
        } else {
            userNavView.setVisibility(View.VISIBLE)
            NavigationUI.setupActionBarWithNavController(
                this, userNavController!!,
                userAppBarConfig!!
            )
        }
    }

    // Handle navigation drawer open/close events
    override fun onSupportNavigateUp(): Boolean {
        val navController: NavController?
        val appBarConfig: AppBarConfiguration?
        if (isAdmin) {
            navController = adminNavController
            appBarConfig = adminAppBarConfig
        } else {
            navController = userNavController
            appBarConfig = userAppBarConfig
        }
        return (NavigationUI.navigateUp(navController!!, appBarConfig!!)
                || super.onSupportNavigateUp())
    }

    // Example method to retrieve the user role from a shared preference or database
    private val userRole: Boolean
        private get() =// TODO: Implement this method
            true // Assume the user is an admin for demonstration purposes

}
