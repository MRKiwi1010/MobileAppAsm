package com.example.mobileappasm

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.style.TtsSpan.ARG_PASSWORD
import android.text.style.TtsSpan.ARG_USERNAME
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
//import com.example.mobileappasm.ui.login.AdminLoginPage.Companion.USERNAME_KEY
import com.example.mobileappasm.ui.login.AdminViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminProfile : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adminViewModel: AdminViewModel

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
        return inflater.inflate(R.layout.fragment_admin_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (loginFragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(loginFragment).commit()
        }

        val viewModel = ViewModelProvider(requireActivity()).get(AdminViewModel::class.java)
        adminViewModel = AdminViewModel()
        database.orderByChild("username").equalTo(adminViewModel.username).addListenerForSingleValueEvent(object : ValueEventListener {
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
