package com.example.mobileappasm.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import com.example.mobileappasm.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.AdminAddChild
import com.example.mobileappasm.Customer
import com.example.mobileappasm.data.model.cusViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

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
//                                var key = "users"+"03d".format(count)
                                var usersCount = 1
                                for (cusSnapshot in dataSnapshot.children) {
                                    var userid = cusSnapshot.key.toString()
                                    val currentCount = userid.substring(5).toInt()
                                    if (currentCount >= usersCount) {
                                        usersCount = currentCount + 1
                                    }}
                                var key = "users" + "%03d".format(usersCount)

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
                                    "userimg" to "",
                                    "password" to "",
                                    "username" to firebaseUser.displayName

                                )
                                usersRef.child(key).setValue(user)
                                updateUI(firebaseUser)

                                val firebaseUser = auth.currentUser
                                val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)

                                val uid = firebaseUser?.uid
                                if (uid != null) {
                                    val storageRef = FirebaseStorage.getInstance().reference.child("user_img/$uid")
                                    storageRef.putFile(firebaseUser.photoUrl!!)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Image uploaded successfully")
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e(TAG, "Image upload failed: ${exception.message}")
                                        }
                                }
                                if (firebaseUser != null) {
                                    val username = firebaseUser.displayName
                                    if (username != null) {
                                        viewModel.setCustomerUsername(username)
                                    } else {
                                    }
                                }
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
            view?.findNavController()?.navigate(R.id.cusMainPage)
        }
    }
    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
    }
}