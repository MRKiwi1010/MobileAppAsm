package com.example.mobileappasm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.AdminAdapter
import com.example.mobileappasm.databinding.FragmentAdminViewAdminListBinding
import com.example.mobileappasm.ui.login.adminViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminViewAdminList : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAdminViewAdminListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adminAdapter: AdminAdapter
    private lateinit var adminList: ArrayList<Admin>

    private lateinit var database: FirebaseDatabase
    private lateinit var adminRef: DatabaseReference

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
        val rootView = inflater.inflate(R.layout.fragment_admin_view_admin_list, container, false)
                drawerLayout = rootView.findViewById(R.id.drawerLayout)
                navView = rootView.findViewById(R.id.navigationView) // initialize the navView property
        //        //        navView.inflateMenu(R.menu.navigation_admin_drawer)
        //
        //        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.adminViewAdminList), drawerLayout)
        setupActionBarWithNavController(requireActivity() as AppCompatActivity, navController, appBarConfiguration)

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


        recyclerView = rootView.findViewById(R.id.adminRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adminAdapter = AdminAdapter(requireContext())
        recyclerView.adapter = adminAdapter

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Admin List"

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

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        // Enable back button callback
//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            // do nothing when the back button is pressed
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        adminRef = database.getReference("admin")

        adminList = ArrayList()
        adminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminList.clear()
                for (userSnapShot in snapshot.children) {
                    val admin = userSnapShot.getValue(Admin::class.java)
                    if (admin != null) {
                        adminList.add(admin)
                    }
                }
                adminAdapter.submitList(adminList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        val viewModel = ViewModelProvider(requireActivity())[adminViewModel::class.java]
        val adminUsername = viewModel.username

        adminRef.orderByChild("username").equalTo(adminUsername).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val admin = snapshot.children.first().getValue(Admin::class.java)

                // check if admin's position is admin
                if (admin != null) {
                    if (admin.position == "Admin") {
                        // set button visibility to visible
                        val button = view.findViewById<Button>(R.id.AddAdmin)
                        button.visibility = View.VISIBLE
                    } else if(admin.position == "staff") {
                        val button = view.findViewById<Button>(R.id.AddAdmin)
                        button.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
            }
        })

        val btnAddAdmin = view.findViewById<Button>(R.id.AddAdmin)
        btnAddAdmin.setOnClickListener{
            findNavController().navigate(R.id.adminAddAdmin)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminViewAdminList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
