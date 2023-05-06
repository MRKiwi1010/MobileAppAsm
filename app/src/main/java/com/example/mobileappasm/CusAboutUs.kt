package com.example.mobileappasm

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mobileappasm.data.model.cusViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CusAboutUs : Fragment() {
    private lateinit var textView6: TextView
    private lateinit var textView62 : TextView
    private lateinit var textView7: TextView

    //drawer
    private lateinit var drawerLayout2: DrawerLayout
    private lateinit var navView2: NavigationView
    private lateinit var appBarConfiguration2: AppBarConfiguration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //drawer
        val rootView = inflater.inflate(R.layout.fragment_cus_about_us, container, false)
        drawerLayout2 = rootView.findViewById(R.id.drawerLayout2)
        navView2 = rootView.findViewById(R.id.navigationView_cus) // initialize the navView property

        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration2 = AppBarConfiguration(setOf(R.id.cusAboutUs), drawerLayout2)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController,
            appBarConfiguration2
        )
        NavigationUI.setupWithNavController(navView2, navController)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Main Page"

        // handle navigation item clicks
        navView2.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cusMainPage -> {
                    view?.findNavController()?.navigate(R.id.cusMainPage)
                    true
                }
                R.id.cusProfile -> {
                    view?.findNavController()?.navigate(R.id.cusProfile)
                    true
                }
                R.id.cusDonatePersonalDetails -> {
                    view?.findNavController()?.navigate(R.id.cusDonatePersonalDetails)
                    true
                }
                R.id.cusTotalDonation -> {
                    view?.findNavController()?.navigate(R.id.cusTotalDonation)
                    true
                }
                R.id.cusDonationHistory -> {
                    view?.findNavController()?.navigate(R.id.cusDonationHistory)
                    true
                }
                R.id.cusAboutUs -> {
                    view?.findNavController()?.navigate(R.id.cusAboutUs)
                    true
                }
                R.id.cusLogout -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { dialog, which ->
                            val viewModel =
                                ViewModelProvider(requireActivity())[cusViewModel::class.java]
                            viewModel.setCustomerUsername("")
                            Firebase.auth.signOut()
                            view?.findNavController()?.navigate(R.id.AAHomePage)

                            // Disable the drawer toggle
                            val actionBar = (activity as AppCompatActivity?)?.supportActionBar
                            actionBar?.setDisplayHomeAsUpEnabled(false)
                            drawerLayout2.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                            // Clear the navigation drawer and remove the hamburger button
                            navView2.menu.clear()
                            actionBar?.setHomeAsUpIndicator(null)
                        }
                        .setNegativeButton("No", null)
                        .show()
                    true
                }
                else -> false
            }
        }

        textView6 = rootView.findViewById(R.id.textView6)
        textView7 = rootView.findViewById(R.id.textView7)
        textView62 = rootView.findViewById(R.id.textView62)

        textView6.setOnClickListener { dialPhoneNumber("60189481671") }
        textView7.setOnClickListener { dialPhoneNumber("60173970217") }
        textView62.setOnClickListener { sendEmail("andyhzy-pm20@student.tarc.edu.my") }

        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // open the navigation drawer when the hamburger icon is clicked
                drawerLayout2?.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Enable back button callback
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Show confirmation dialog before exiting the app
            AlertDialog.Builder(requireContext())
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes") { _, _ ->
                    requireActivity().finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Check if the new theme is a light theme
        if ((newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            // Set the title and navigation bar color for the light theme
            requireActivity().actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.light_theme_color)))
            requireActivity().window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.light_theme_color)
        } else {
            // Set the title and navigation bar color for the dark theme
            requireActivity().actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_theme_color)))
            requireActivity().window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.dark_theme_color)
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(dialIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to dial phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(emailAddress: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$emailAddress")
        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to send email", Toast.LENGTH_SHORT).show()
        }
    }
}
