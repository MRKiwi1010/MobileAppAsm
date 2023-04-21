package com.example.mobileappasm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.R
import com.example.mobileappasm.ui.login.CusLoginPage
import com.google.firebase.database.*

class CusForgetPass : Fragment() {

    private lateinit var resetEmail: EditText
    private lateinit var resetPassword: EditText
    private lateinit var reset_button: Button
    private lateinit var loginRedirectText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cus_forget_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetEmail = view.findViewById(R.id.reset_email)
        resetPassword = view.findViewById(R.id.reset_password)
        reset_button = view.findViewById(R.id.reset_button)
        loginRedirectText = view.findViewById(R.id.loginRedirectText)
        reset_button.setOnClickListener {
            if (!validateUseremail() or !validatePassword()) {
            } else {
                checkUser()
            }
        }

        loginRedirectText.setOnClickListener{
            view.findNavController().navigate(R.id.cusLoginPage)
        }

    }


    private fun validateUseremail(): Boolean {
        val `val` = resetEmail!!.text.toString()
        return if (`val`.isEmpty()) {
            resetEmail!!.error = "Email cannot be empty"
            false
        } else {
            resetEmail!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = resetPassword.text.toString()
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$".toRegex()
        return if (password.isEmpty()) {
            resetPassword.error = "Password cannot be empty"
            false
        } else if (password.contains(" ")) {
            resetPassword.error = "Password cannot contain spaces"
            false
        } else if (!pattern.matches(password)) {
            resetPassword.error = "Password should be at least 8 characters long and contain at least one uppercase and one lowercase letter"
            false
        } else {
            resetPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val userEmail = resetEmail!!.text.toString().trim { it <= ' ' }
        val userPassword = resetPassword!!.text.toString().trim { it <= ' ' }
        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase: Query = reference.orderByChild("email").equalTo(userEmail)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    resetEmail!!.error = null
                    val userNode = snapshot.child(userEmail)
                    val userPasswordFromDB = userNode.child("password").getValue(String::class.java)
                    if (userPasswordFromDB == userPassword) {
                        // User entered the same password as in the database, no need to update
                        val toast = Toast.makeText(activity, "Password is same. No changes will be made", Toast.LENGTH_SHORT)
                        toast.show()
                        // Navigate to CusLoginPage fragment using Navigation Component
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(R.id.cusLoginPage)
                        }, 2000)
                    } else {
                        // Update password
                        val updatedUserData = HashMap<String, Any>()
                        updatedUserData["password"] = userPassword
                        reference.child(userNode.key!!).updateChildren(updatedUserData)
                        val toast = Toast.makeText(activity, "Password changed successfully", Toast.LENGTH_SHORT)
                        toast.show()
                        // Navigate to CusLoginPage fragment using Navigation Component
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(R.id.cusLoginPage)
                        }, 2000)
                    }
                } else {
                    resetEmail!!.error = "User Email does not exist"
                    resetEmail!!.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
