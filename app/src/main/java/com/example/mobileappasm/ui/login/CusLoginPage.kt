package com.example.mobileappasm.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import com.example.mobileappasm.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mobileappasm.Customer
import com.example.mobileappasm.data.model.cusViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
class CusLoginPage : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
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
        val view = inflater.inflate(R.layout.fragment_cus_login_page, container, false)


        // Initialize your views and variables here
        loginUsername = view.findViewById(R.id.login_username)
        loginPassword = view.findViewById(R.id.login_password)
        loginButton = view.findViewById(R.id.login_button)
        signupRedirectText = view.findViewById(R.id.signupRedirectText)
        forgotRedirectText = view.findViewById(R.id.forgotRedirectText)
        val TextArea33 = view.findViewById<LinearLayout>(R.id.TextArea33)

        auth = Firebase.auth
        database = Firebase.database.reference

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1010006804829-3169corfrgnncmo3c04hrt14o004bats.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        TextArea33.setOnClickListener {
            signIn()
        }


        loginButton.setOnClickListener {
            if (!validateUsername() or !validatePassword()) {
            }
            else {
                val userUsername = loginUsername!!.text.toString().trim { it <= ' ' }
                val userPassword = loginPassword!!.text.toString().trim { it <= ' ' }
                val reference = FirebaseDatabase.getInstance().reference.child("users")
                val checkUserDatabase: Query = reference.orderByChild("username").equalTo(userUsername)

                val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)


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
                                view.findNavController().navigate(R.id.cusMainPage)

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
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val usersRef = database.child("users")
                    val uid = firebaseUser?.uid
                    if (uid != null) {
                        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var count = dataSnapshot.childrenCount.toInt() + 1
                                var key = "users$count"
                                dataSnapshot.children.forEach { childSnapshot ->
                                    val user = if (childSnapshot.value is HashMap<*, *>) {
                                        childSnapshot.getValue(Customer::class.java)
                                    } else {
                                        null
                                    }
                                    if (user != null && user.username == firebaseUser.displayName) {
                                        key = childSnapshot.key ?: key
                                        count--
                                        return@forEach
                                    }
                                }
                                val user = hashMapOf(
                                    "email" to firebaseUser.email,
                                    "name" to firebaseUser.displayName,
                                    "userimg" to firebaseUser.photoUrl.toString(),
                                    "username" to firebaseUser.displayName

                                )
                                usersRef.child(key).setValue(user)
                                updateUI(firebaseUser)
                                val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
                                viewModel.setCustomerUsername(firebaseUser.displayName ?: "username")
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.w(TAG, "addNewUser:onCancelled", databaseError.toException())
                            }
                        })
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Navigate to the next screen using NavController
            //view?.findNavController()?.navigate(R.id.cusSignInGoogleTest)
            view?.findNavController()?.navigate(R.id.cusDonationHistory)
        }
    }
    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
    }


}