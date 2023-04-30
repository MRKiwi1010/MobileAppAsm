package com.example.mobileappasm

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private lateinit var navView: NavigationView
    private var adminNavController: NavController? = null
    private var userNavController: NavController? = null
    private var adminAppBarConfig: AppBarConfiguration? = null
    private var userAppBarConfig: AppBarConfiguration? = null
    private var isAdmin = false // flag to indicate whether the user is an admin or not

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        setUpNavBar()
    }

    private fun setUpNavBar() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        /// Retrieve the isAdmin flag from shared preferences or database
        val sharedPref = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        isAdmin = sharedPref.getBoolean("is_admin", false)
        val isUser = sharedPref.getBoolean("is_User", false)

//        isAdmin = false
//        val isUser = false

        if (isAdmin) {
            navView.menu.clear()
            navView.inflateMenu(R.menu.navigation_admin_drawer)
            val navController = this.findNavController(R.id.myNavHostFragment)
            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
            NavigationUI.setupWithNavController(navView, navController)
        } else if (isUser){
            navView.menu.clear()
            navView.inflateMenu(R.menu.navdrawer_menu)
            val navController = this.findNavController(R.id.myNavHostFragment)
            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
            NavigationUI.setupWithNavController(navView, navController)
        } else {
            navView.visibility = View.GONE
            navView.menu.clear()
        }
    }

    // Handle navigation drawer open/close events
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}
