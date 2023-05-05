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
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.Query

class CusLoginPage : Fragment() {

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView
    private lateinit var forgotRedirectText: TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_cus_login_page, container, false)
        loginUsername = view.findViewById(R.id.login_username)
        loginPassword = view.findViewById(R.id.login_password)
        loginButton = view.findViewById(R.id.login_button)
        signupRedirectText = view.findViewById(R.id.signupRedirectText)
        forgotRedirectText = view.findViewById(R.id.forgotRedirectText)

        loginButton.setOnClickListener {
            if (!validateUsername() or !validatePassword()) {

            }
            else {
                val userUsername = loginUsername!!.text.toString().trim { it <= ' ' }
                val userPassword = loginPassword!!.text.toString().trim { it <= ' ' }
                val reference = FirebaseDatabase.getInstance().reference.child("users")
                val checkUserDatabase: Query = reference.orderByChild("username").equalTo(userUsername)

                val viewModel = ViewModelProvider(requireActivity()).get(
                    cusViewModel::class.java)

                checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            loginUsername!!.error = null
                            val passwordFromDB = snapshot.children.firstOrNull()?.child("password")?.getValue(String::class.java)

                            if (passwordFromDB == userPassword) {
                                loginUsername!!.error = null

                                val usernameFromDB = snapshot.children.firstOrNull()?.child("username")?.getValue(String::class.java)

                                if (usernameFromDB != null) {
                                    viewModel.setCustomerUsername(usernameFromDB)
                                }
                                view.findNavController().navigate(R.id.cusDonationHistory)

                            } else {
                                loginPassword!!.error = "Invalid Credentials"
                                loginPassword!!.requestFocus()
                            }
                        } else {
                            loginUsername!!.error = "User does not exist"
                            loginUsername!!.requestFocus()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }


        signupRedirectText.setOnClickListener {
            view.findNavController().navigate(R.id.cusSignUp)
        }

        forgotRedirectText.setOnClickListener{
            view.findNavController().navigate(R.id.cusForgetPass)
        }
        return view
    }

    private fun validateUsername(): Boolean {
        val `val` = loginUsername!!.text.toString()
        return if (`val`.isEmpty()) {
            loginUsername!!.error = "Username cannot be empty"
            false
        } else {
            loginUsername!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val `val` = loginPassword!!.text.toString()
        return if (`val`.isEmpty()) {
            loginPassword!!.error = "Password cannot be empty"
            false
        } else {
            loginPassword!!.error = null
            true
        }
    }
}