package com.example.mobileappasm

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class CusDonatePersonalDetails : Fragment() {
    private lateinit var childName: String

    //drawer
    private lateinit var drawerLayout2: DrawerLayout
    private lateinit var navView2: NavigationView
    private lateinit var appBarConfiguration2: AppBarConfiguration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_cus_donate_personal_details, container, false)

        //Rename the fragment
        //drawer
        val rootView = inflater.inflate(R.layout.fragment_cus_donate_personal_details, container, false)
        drawerLayout2 = rootView.findViewById(R.id.drawerLayout2)
        navView2 = rootView.findViewById(R.id.navigationView_cus) // initialize the navView property

        // enable the navigation drawer
        setHasOptionsMenu(true)

        // get the NavController for this fragment
        val navController = this.findNavController()

        // set up the ActionBar and the NavigationView with the NavController
        appBarConfiguration2 = AppBarConfiguration(setOf(R.id.cusDonatePersonalDetails), drawerLayout2)
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

        val context: Context? = activity
        val database = FirebaseDatabase.getInstance().reference
        val childTable = database.child("child")
        childTable.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childNames = ArrayList<String>()
                val imageUrls = ArrayList<String>()

                for (childSnapshot in dataSnapshot.children) {
                    val childName = childSnapshot.child("childName").value as String
                    val childImageUrl = childSnapshot.child("childUrl").value as String
                    childNames.add(childName)
                    imageUrls.add(childImageUrl)
                }

                val gridView = rootView.findViewById<GridView>(R.id.gridView)
                gridView.adapter = CustomGridAdapter(activity, childNames, imageUrls, rootView)
                gridView.setOnItemClickListener { _, _, position, _ ->
                    // Handle click event for each item in the grid view
                    val childName = childNames[position]
                    val childKey = dataSnapshot.children.elementAt(position).key

                    // Navigate to the CusViewChild fragment and pass the selected childName and childKey
                    val viewModel = ViewModelProvider(context as FragmentActivity).get(cusViewModel::class.java)
                    viewModel.setchildname(childName)
                    if (childKey != null) {
                        viewModel.setChildKey(childKey)
                    }
                    rootView.findNavController().navigate(R.id.cusViewChild)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        childName = arguments?.getString("childName") ?: ""
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


    class CustomGridAdapter(
        private val activity: FragmentActivity?,
        private val childNames: ArrayList<String>,
        private val imageUrls: ArrayList<String>,
        private val view: View
    ) :
        ArrayAdapter<String>(activity!!, R.layout.cus_view_child_grid, childNames) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val viewHolder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.cus_view_child_grid, parent, false)
                viewHolder = ViewHolder()
                viewHolder.imageView = convertView.findViewById(R.id.grid_image)
                viewHolder.textView = convertView.findViewById(R.id.item_name)
                viewHolder.button = convertView.findViewById(R.id.button2)
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }
            viewHolder.textView.text = childNames[position]

            // Load image from Firebase storage based on the URL
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrls[position])
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity!!).load(uri).into(viewHolder.imageView)
            }

            viewHolder.button.setOnClickListener {
                val childname = childNames[position]
                activity?.let {
                    val viewModel = ViewModelProvider(it).get(cusViewModel::class.java)
                    viewModel.setchildname(childname)
                    view.findNavController().navigate(R.id.cusViewChild)
                }
            }
            return convertView!!
        }
        private class ViewHolder {
            lateinit var imageView: ImageView
            lateinit var textView: TextView
            lateinit var button: Button
        }
    }
}
