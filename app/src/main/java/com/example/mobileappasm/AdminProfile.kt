package com.example.mobileappasm

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.style.TtsSpan.ARG_PASSWORD
import android.text.style.TtsSpan.ARG_USERNAME
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
//import com.example.mobileappasm.ui.login.AdminLoginPage.Companion.USERNAME_KEY
import com.example.mobileappasm.ui.login.adminViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminProfile : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adminViewModel: adminViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("admin")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_admin_profile, container, false)
        drawerLayout = rootView.findViewById(R.id.drawerLayout)
        navView = rootView.findViewById(R.id.navigationView) // initialize the navView property
        navView.inflateMenu(R.menu.navigation_admin_drawer)

        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.adminProfile), drawerLayout)
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
                else -> false
            }
        }

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

        val loginFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (loginFragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(loginFragment).commit()
        }

        adminViewModel = adminViewModel()
        val viewModel = ViewModelProvider(requireActivity()).get(adminViewModel::class.java)

        database.orderByChild("username").equalTo(viewModel.username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "snapshot: $snapshot")
                if (snapshot.exists()) {
                    val admin = snapshot.children.first().getValue(Admin::class.java)

                    admin?.let {
                        val welcomeMessage = view.findViewById<TextView>(R.id.welcome)
                        welcomeMessage.text = "Welcome, ${admin.username}!"

                        val nameTextView = view.findViewById<TextView>(R.id.name)
                        nameTextView.text = admin.name

                        val emailTextView = view.findViewById<TextView>(R.id.email)
                        emailTextView.text = admin.email

                        val contactTextView = view.findViewById<TextView>(R.id.contact)
                        contactTextView.text = admin.contact

                        val genderTextView = view.findViewById<TextView>(R.id.gender)
                        genderTextView.text = admin.gender

                        val ageTextView = view.findViewById<TextView>(R.id.age)
                        ageTextView.text = admin.age

                        val positionTextView = view.findViewById<TextView>(R.id.position)
                        positionTextView.text = admin.position

                        val imageView = view.findViewById<ImageView>(R.id.profile_image)
                        admin.imageUri?.let { imageUri ->
                            Glide.with(requireContext()).load(imageUri).into(imageView)
                        }
                    }
                }else
                {
                    Toast.makeText(requireContext(), "No Admin Found", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

//    override fun onSupportNavigateUp(): Boolean {
//        // Handle the back button event
//        val navController = findNavController()
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }


    companion object {
//        const val ARG_USERNAME = "username"
//        const val ARG_PASSWORD = "password"

        @JvmStatic
        fun newInstance(username: String, password: String): AdminProfile {
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putString(ARG_PASSWORD, password)
            }
            val fragment = AdminProfile().apply {
                arguments = args
            }
            return fragment
        }
    }
}
