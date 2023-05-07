package com.example.mobileappasm

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class CusForgetPass : Fragment() {

    private lateinit var resetUsername: EditText
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

        resetUsername = view.findViewById(R.id.reset_username)
        resetPassword = view.findViewById(R.id.reset_password)
        reset_button = view.findViewById(R.id.donate_button)
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
        val `val` = resetUsername!!.text.toString()
        return if (`val`.isEmpty()) {
            resetUsername!!.error = "Username cannot be empty"
            false
        } else {
            resetUsername!!.error = null
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
        val username = resetUsername!!.text.toString().trim { it <= ' ' }
        val userPassword = resetPassword!!.text.toString().trim { it <= ' ' }
        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase: Query = reference.orderByChild("username").equalTo(username)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    resetUsername!!.error = null
                    var userNode: DataSnapshot? = null
                    for (childSnapshot in snapshot.children) {
                        if (childSnapshot.child("username").getValue(String::class.java) == username) {
                            userNode = childSnapshot
                            break
                        }
                    }
                    val userPasswordFromDB = userNode?.child("password")?.getValue(String::class.java)
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
                        reference.child(userNode!!.key!!).updateChildren(updatedUserData)
                        val toast = Toast.makeText(activity, "Password changed successfully", Toast.LENGTH_SHORT)
                        toast.show()
                        // Navigate to CusLoginPage fragment using Navigation Component
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(R.id.cusLoginPage)
                        }, 2000)
                    }
                } else {
                    resetUsername!!.error = "Username does not exist"
                    resetUsername!!.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}