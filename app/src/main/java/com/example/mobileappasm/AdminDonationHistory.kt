package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.CustomerAdapter
import com.example.mobileappasm.Adapter.DonationAdapter
import com.example.mobileappasm.databinding.FragmentAdminCustomerListBinding
import com.example.mobileappasm.databinding.FragmentAdminDonationHistoryBinding
import com.example.mobileappasm.ui.login.adminViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminDonationHistory : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAdminDonationHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private lateinit var donationList: ArrayList<Donation>

    private lateinit var database: FirebaseDatabase
    private lateinit var donationRef: DatabaseReference

    //drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //drawer
        val rootView = inflater.inflate(R.layout.fragment_admin_donation_history, container, false)
        drawerLayout = rootView.findViewById(R.id.drawerLayout)
        navView = rootView.findViewById(R.id.navigationView) // initialize the navView property
        //        navView.inflateMenu(R.menu.navigation_admin_drawer)

        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.adminDonationHistory), drawerLayout)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController,
            appBarConfiguration
        )

        NavigationUI.setupWithNavController(navView, navController)

        // handle navigation item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.adminProfile -> {
                    // Handle click for adminProfile item
                    view?.findNavController()?.navigate(R.id.adminProfile)
                    true
                }
                R.id.adminViewAdminList -> {
                    // Handle click for adminViewAdminList item
                    view?.findNavController()?.navigate(R.id.adminViewAdminList)
                    true
                }
                R.id.adminChildList -> {
                    // Handle click for adminChildList item
                    view?.findNavController()?.navigate(R.id.adminChildList)
                    true
                }
                R.id.adminCustomerList -> {
                    // Handle click for adminCustomerList item
                    view?.findNavController()?.navigate(R.id.adminCustomerList)
                    true
                }
                R.id.adminDonationHistory -> {
                    // Handle click for adminDonationHistory item
                    view?.findNavController()?.navigate(R.id.adminDonationHistory)
                    true
                }
                R.id.adminLogout -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { dialog, which ->
                            // Implement your logout logic here
                            // For example, you could clear the user's session data and navigate them to the login screen.
                            // You can use the following code to navigate to the login screen:
                            val viewModel = ViewModelProvider(requireActivity())[adminViewModel::class.java]
                            viewModel.username = ""
                            view?.findNavController()?.navigate(R.id.AAHomePage)

                            // Disable the drawer toggle
                            val actionBar = (activity as AppCompatActivity?)?.supportActionBar
                            actionBar?.setDisplayHomeAsUpEnabled(false)
                            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                            // Clear the navigation drawer and remove the hamburger button
                            navView.menu.clear()
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
        donationAdapter = DonationAdapter(requireContext())
        recyclerView.adapter = donationAdapter

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Donation List"


        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // open the navigation drawer when the hamburger icon is clicked
                drawerLayout?.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        donationRef = database.getReference("payment")

        donationList = ArrayList()
        donationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donationList.clear()
                for (userSnapShot in snapshot.children) {
                    val donation = userSnapShot.getValue(Donation::class.java)
                    if (donation != null) {
                        donationList.add(donation)
                    }
                }
                donationAdapter.submitList(donationList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminDonationHistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}