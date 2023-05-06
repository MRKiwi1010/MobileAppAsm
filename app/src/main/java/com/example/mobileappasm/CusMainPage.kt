package com.example.mobileappasm

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobileappasm.Adapter.ItemsAdapter
import com.example.mobileappasm.Domain.ItemsDomain
import com.example.mobileappasm.data.model.cusViewModel
import com.example.mobileappasm.ui.login.adminViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class CusMainPage : Fragment() {

    private lateinit var recyclerViewPopular: RecyclerView
    private lateinit var recyclerViewNew: RecyclerView
    private lateinit var adapterPopular: RecyclerView.Adapter<*>
    private lateinit var adapterNew: RecyclerView.Adapter<*>
    private lateinit var textView4 : TextView
    private lateinit var imageView2: ImageView
    private lateinit var textView71 : TextView
    private lateinit var textView73 : TextView
    private lateinit var textView52: TextView
    private lateinit var textView54: TextView
    private lateinit var textView56: TextView
    private lateinit var textView58: TextView

    //drawer
    private lateinit var drawerLayout2: DrawerLayout
    private lateinit var navView2: NavigationView
    private lateinit var appBarConfiguration2: AppBarConfiguration


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_cus_main_page, container, false)
        drawerLayout2 = rootView.findViewById(R.id.drawerLayout2)
        navView2 = rootView.findViewById(R.id.navigationView_cus) // initialize the navView property
        val firstLayout = rootView.findViewById<ConstraintLayout>(R.id.firstlayout)
        val secondlayout = rootView.findViewById<ConstraintLayout>(R.id.secondlayout)
        val thirdlayout = rootView.findViewById<ConstraintLayout>(R.id.thirdlayout)
        val fourlayout = rootView.findViewById<ConstraintLayout>(R.id.fourlayout)


        firstLayout.setOnClickListener {
            rootView.findNavController().navigate(R.id.cusDonatePersonalDetails)
        }
        secondlayout.setOnClickListener {
            rootView.findNavController().navigate(R.id.cusTotalDonation)
        }
        thirdlayout.setOnClickListener {
            rootView.findNavController().navigate(R.id.cusAboutUs)
        }
        fourlayout.setOnClickListener {
            rootView.findNavController().navigate(R.id.cusDonationHistory)
        }


        textView73= rootView.findViewById(R.id.textView73)
        // this is the child text view
        textView52 = rootView.findViewById(R.id.textView52)
// donate now
        textView54 = rootView.findViewById(R.id.textView54)
// about us
        textView56 = rootView.findViewById(R.id.textView56)
// history
        textView58 = rootView.findViewById(R.id.textView58)


        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration2 = AppBarConfiguration(setOf(R.id.cusMainPage), drawerLayout2)
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

        recyclerViewPopular = rootView.findViewById(R.id.viewPupolar)
        recyclerViewNew = rootView.findViewById(R.id.viewNew)
        recyclerViewPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNew.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        textView4 = rootView.findViewById(R.id.textView4)
        imageView2 = rootView.findViewById(R.id.imageView2)
        textView71 = rootView.findViewById(R.id.textView71)


        imageView2.setOnClickListener{
            rootView.findNavController().navigate(R.id.cusProfile)
        }

        textView71.setOnClickListener {
            rootView.findNavController().navigate(R.id.cusDonatePersonalDetails)
        }
        textView73.setOnClickListener {
            rootView.findNavController().navigate(R.id.cusDonatePersonalDetails)
        }




        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val username = viewModel.getCustomerUsername()
        textView4.text = username

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val childReference = databaseReference.child(username)
//        childReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get child information from Firebase Realtime Database
//                val user_name = dataSnapshot.child("username").value.toString()
//                val userimg = dataSnapshot.child("userimg").value.toString()
//
//
//                Glide.with(requireContext()).load(userimg).into(imageView2)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error
//            }
//        })
        childReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get child information from Firebase Realtime Database
                val firebaseUsername = dataSnapshot.child("username").value.toString()
                val userimg = dataSnapshot.child("userimg").value.toString()

                if (firebaseUsername == username) {
                    textView4.text = firebaseUsername
                    Glide.with(requireContext()).load(userimg).into(imageView2)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        val itemsArraylist = ArrayList<ItemsDomain>()
        val itemsArraylist2 = ArrayList<ItemsDomain>()
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child("child").orderByKey()

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Loop through the data and add it to the itemsArraylist
                for (childSnapshot in dataSnapshot.children) {
//                    val childAge = childSnapshot.child("childAge").getValue(Int::class.java)
                    val childName = childSnapshot.child("childName").getValue(String::class.java)
                    val childNation = childSnapshot.child("childNation").getValue(String::class.java)
//                    val child_Des = childSnapshot.child("child_Des").getValue(String::class.java)
                    val target = childSnapshot.child("target").getValue(Int::class.java)?.toInt() ?: 0
                    val  childimg= childSnapshot.child("childUrl").getValue(String::class.java)
                    val totalReceived = childSnapshot.child("totalReceived").getValue(Int::class.java)?.toInt() ?: 0

//                    val item = ItemsDomain(childName!!, childNation!!,"",totalReceived!!,0, childimg!!,0 )
//                    itemsArraylist.add(item)
//                    itemsArraylist2.add(item)

                    if (target < totalReceived) {
                        val item = ItemsDomain(childName!!, childNation!!, "", totalReceived!!, target!!, childimg!!, 0)
                        itemsArraylist.add(item)
                    }
                    if (target > totalReceived) {
                        val item2 = ItemsDomain(childName!!, childNation!!, "", totalReceived!!, target!!, childimg!!, 0)
                        itemsArraylist2.add(item2)
                    }
                }

                // Update the adapter with the new data
                adapterNew.notifyDataSetChanged()
                adapterPopular.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        adapterNew = ItemsAdapter(itemsArraylist)
        adapterPopular = ItemsAdapter(itemsArraylist2)

        recyclerViewNew.adapter = adapterNew
        recyclerViewPopular.adapter = adapterPopular

        adapterNew.notifyDataSetChanged()
        adapterPopular.notifyDataSetChanged()

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
}



