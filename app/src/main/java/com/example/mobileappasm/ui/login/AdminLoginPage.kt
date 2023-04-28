package com.example.mobileappasm.ui.login

import com.example.mobileappasm.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mobileappasm.ui.login.AdminViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class AdminLoginPage : Fragment() {

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button

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
                val reference = FirebaseDatabase.getInstance().getReference("admin")
                val checkUserDatabase: Query = reference.orderByChild("username").equalTo(userUsername)

                val viewModel = ViewModelProvider(requireActivity()).get(AdminViewModel::class.java)

                checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            loginUsername!!.error = null
                            val passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java)
                            if (passwordFromDB.toString() == userPassword) {
                                loginUsername.error = null

                                val usernameFromDB = snapshot.child(userUsername).child("username").getValue(
                                    String::class.java
                                )

                                if (usernameFromDB != null) {
                                    viewModel.username = usernameFromDB
                                }

                                view.findNavController().navigate(R.id.adminProfile)

                            }
                            else {
                                loginPassword.error = "Invalid Credentials"
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


