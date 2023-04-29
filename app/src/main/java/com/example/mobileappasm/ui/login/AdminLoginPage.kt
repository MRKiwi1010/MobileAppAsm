package com.example.mobileappasm.ui.login

import android.content.Context
import com.example.mobileappasm.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mobileappasm.Admin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminLoginPage : Fragment() {

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adminViewModel: adminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("admin")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_login_page, container, false)
        loginUsername = view.findViewById(R.id.username)
        loginPassword = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login)

        loginButton.setOnClickListener {
            if (!validateUsername() or !validatePassword()) {

            } else {
                val userUsername = loginUsername.text.toString().trim() { it <= ' ' }
                val userPassword = loginPassword.text.toString().trim() { it <= ' ' }

                val checkUserDatabase: Query = database.orderByChild("username").equalTo(userUsername)

                adminViewModel = adminViewModel()
                val viewModel = ViewModelProvider(requireActivity())[adminViewModel::class.java]

                checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            loginUsername!!.error = null
                            val admin = snapshot.children.first().getValue(Admin::class.java)
                            if (admin != null) {
                                if (admin.password != null) {
                                        if (admin.password == userPassword) {
                                            loginUsername.error = null

                                            val usernameFromDB = snapshot.child(userUsername).child("username").getValue(
                                                Admin::class.java
                                            )

                                            if (usernameFromDB != null) {
                                                viewModel.username = admin.username
                                                val sharedPref = requireContext().getSharedPreferences("my_pref", Context.MODE_PRIVATE)
                                                sharedPref.edit().putBoolean("is_admin", true).apply() // replace true with false for user login
                                            }

                                            viewModel.username = admin.username
                                            val sharedPref = requireContext().getSharedPreferences("my_pref", Context.MODE_PRIVATE)
                                            sharedPref.edit().putBoolean("is_admin", true).apply() // replace true with false for user login

                                            view.findNavController().navigate(R.id.adminProfile)

                                        } else {
                                            loginPassword.error = "Invalid Credentials"
                                            loginPassword.requestFocus()
                                        }
                                    }
                                } else{
                                    loginPassword.error = "No password"
                                    loginPassword.requestFocus()
                                }
                        }
                        else {
                            loginUsername.error = "User does not exist"
                            loginUsername.requestFocus()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

        return view
    }

    private fun validateUsername(): Boolean {
        val `val` = loginUsername.text.toString()
        return if (`val`.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val `val` = loginPassword.text.toString()
        return if (`val`.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }
}


