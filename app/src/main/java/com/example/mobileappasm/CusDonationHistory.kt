package com.example.mobileappasm

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.cusDonationAdapter
import com.example.mobileappasm.data.model.cusViewModel
import com.example.mobileappasm.databinding.FragmentAdminDonationHistoryBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class CusDonationHistory : Fragment() {
    private lateinit var binding: FragmentAdminDonationHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var cusDonationAdapter: cusDonationAdapter
    private lateinit var donationList: ArrayList<Donation>

    //drawer
    private lateinit var drawerLayout2: DrawerLayout
    private lateinit var navView2: NavigationView
    private lateinit var appBarConfiguration2: AppBarConfiguration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//drawer
        val rootView = inflater.inflate(R.layout.fragment_cus_donation_history, container, false)
        drawerLayout2 = rootView.findViewById(R.id.drawerLayout2)
        navView2 = rootView.findViewById(R.id.navigationView_cus) // initialize the navView property

        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration2 = AppBarConfiguration(setOf(R.id.cusDonationHistory), drawerLayout2)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController,
            appBarConfiguration2
        )
        NavigationUI.setupWithNavController(navView2, navController)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Donation History"

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

//        binding = FragmentAdminDonationHistoryBinding.inflate(inflater, container, false)
        recyclerView = rootView.findViewById(R.id.donationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        cusDonationAdapter = cusDonationAdapter(requireContext())
        recyclerView.adapter = cusDonationAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("payment")
        val query: Query = databaseReference.orderByChild("username").equalTo(customerUsername)
        donationList = ArrayList()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donationList.clear()
                for (userSnapShot in snapshot.children) {
                    val donation = userSnapShot.getValue(Donation::class.java)
                    if (donation != null) {
                        donationList.add(donation)
                    }
                }
                cusDonationAdapter.submitList(donationList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}
