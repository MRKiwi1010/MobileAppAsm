package com.example.mobileappasm

import com.example.mobileappasm.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mobileappasm.ui.login.CusLoginPage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CusSignUpFragment : Fragment() {

    private lateinit var signupName: EditText
    private lateinit var signupUsername: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText
    private lateinit var loginRedirectText: TextView
    private lateinit var signupButton: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_sign_up, container, false)

        signupName = view.findViewById(R.id.signup_name)
        signupEmail = view.findViewById(R.id.signup_email)
        signupUsername = view.findViewById(R.id.signup_username)
        signupPassword = view.findViewById(R.id.signup_password)
        loginRedirectText = view.findViewById(R.id.loginRedirectText)
        signupButton = view.findViewById(R.id.signup_button)


        signupButton.setOnClickListener {
            database = FirebaseDatabase.getInstance()
            reference = database!!.getReference("users")
            val name = signupName.text.toString()
            val email = signupEmail.text.toString()
            val username = signupUsername.text.toString()
            val password = signupPassword.text.toString()
            val helperClass = HelperClass(name, email, username, password)
            reference!!.child(username).setValue(helperClass)
            Toast.makeText(
                requireContext(),
                "You have signed up successfully!",
                Toast.LENGTH_SHORT
            ).show()
            view.findNavController().navigate(R.id.cusSignUp)
        }


        loginRedirectText.setOnClickListener {
            view.findNavController().navigate(R.id.cusLoginPage)

        }

        return view
    }

}


