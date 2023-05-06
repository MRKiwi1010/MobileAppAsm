package com.example.mobileappasm

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class CusSignUp : Fragment() {
    private lateinit var signupName: EditText
    private lateinit var signupUsername: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText
    private lateinit var loginRedirectText: TextView
    private lateinit var signupButton: Button
    private lateinit var checkBox: CheckBox
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
        checkBox = view.findViewById(R.id.checkBox)


        signupButton.setOnClickListener {
            if (!validatename() or !validateemail() or !validateUsername() or !validatePassword())  {
                checkUser()
            } else {
                addUser()
            }

        }

        loginRedirectText.setOnClickListener {
            view.findNavController().navigate(R.id.cusLoginPage)

        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Sign Up"

        setHasOptionsMenu(true)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validatename(): Boolean {
        val name = signupName.text.toString().trim()
        return if (name.isEmpty()) {
            signupName.error = "Full Name cannot be empty"
            false
        } else if (!name.matches("^[a-zA-Z]*$".toRegex())) {
            signupName.error = "Full Name should only contain letters"
            false
        } else {
            signupName.error = null
            true
        }
    }


    private fun validateemail(): Boolean {
        val email = signupEmail.text.toString()
        val emailPattern = Patterns.EMAIL_ADDRESS
        return if (email.isEmpty()) {
            signupEmail.error = "Email cannot be empty"
            false
        } else if (!emailPattern.matcher(email).matches()) {
            signupEmail.error = "Invalid email address"
            false
        } else {
            signupEmail.error = null
            true
        }
    }

    private fun validateUsername(): Boolean {
        val username = signupUsername.text.toString().trim()
        return if (username.isEmpty()) {
            signupUsername.error = "Username cannot be empty"
            false
        } else if (!username.matches("^[a-zA-Z0-9]*$".toRegex())) {
            signupUsername.error = "Username should only contain letters and numbers"
            false
        } else {
            signupUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = signupPassword.text.toString()
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$".toRegex()
        return if (password.isEmpty()) {
            signupPassword.error = "Password cannot be empty"
            false
        } else if (password.contains(" ")) {
            signupPassword.error = "Password cannot contain spaces"
            false
        } else if (!pattern.matches(password)) {
            signupPassword.error = "Password should be at least 8 characters long and contain at least one uppercase and one lowercase letter"
            false
        } else {
            signupPassword.error = null
            true
        }
    }
    private fun checkUser() {
        val userEmail = signupEmail!!.text.toString().trim()
        val userName = signupUsername!!.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("users")

        val checkEmailQuery: Query = reference.orderByChild("email").equalTo(userEmail)
        checkEmailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    signupEmail!!.error = "Email already exists"
                    signupEmail!!.requestFocus()
                } else {
                    val checkUsernameQuery: Query = reference.orderByChild("username").equalTo(userName)
                    checkUsernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                signupUsername!!.error = "Username already exists"
                                signupUsername!!.requestFocus()
                            } else {
                                // User doesn't exist, continue with sign up process
                                // ...
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }



    private fun addUser() {
        if (checkBox.isChecked) {
            database = FirebaseDatabase.getInstance()
            reference = database!!.getReference("users")
            val userfullname = signupName.text.toString()
            val useremail = signupEmail.text.toString()
            val username = signupUsername.text.toString()
            val userpassword = signupPassword.text.toString()
            val helperClass = HelperClass(userfullname, useremail, username, userpassword)
            reference!!.child(username).setValue(helperClass)
            Toast.makeText(
                requireContext(),
                "You have registered successfully!",
                Toast.LENGTH_SHORT
            ).show()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.cusLoginPage)
            }, 1000)
        }else
            Toast.makeText(
                requireContext(),
                "Please agree the terms and conditions",
                Toast.LENGTH_SHORT
            ).show()

    }

}


