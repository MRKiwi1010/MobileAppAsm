package com.example.mobileappasm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mobileappasm.databinding.FragmentAdminAddAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminAddAdmin : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentAdminAddAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAdminAddAdminBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val btnAddAdmin = binding.btnAddAdmin
        btnAddAdmin.setOnClickListener {
            database.child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val adminCount = snapshot.childrenCount.toInt() + 1
                    val adminId = "admin" + "%03d".format(adminCount)

                    val name = binding.adminName.text.toString()
                    val email = binding.adminEmail.text.toString()
                    val username = binding.adminUsername.text.toString()
                    val password = binding.adminPassword.text.toString()
                    val contact = binding.adminContact.text.toString()
                    val gender = when(binding.adminGender.checkedRadioButtonId) {
                        R.id.maleBtn -> "Male"
                        R.id.femaleBtn -> "Female"
                        else -> ""
                    }
                    val age = binding.adminAge.text.toString()

                    // Check if email or username already exists
                    var isEmailUsed = false
                    var isUsernameUsed = false
                    for (adminSnapshot in snapshot.children) {
                        val admin = adminSnapshot.getValue(Admin::class.java)
                        if (admin?.email == email) {
                            isEmailUsed = true
                        }
                        if (admin?.username == username) {
                            isUsernameUsed = true
                        }
                    }

                    if (isEmailUsed) {
                        Toast.makeText(requireContext(), "Email already in use", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (isUsernameUsed) {
                        Toast.makeText(requireContext(), "Username already in use", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val admin = Admin(name, email, username, password, contact, gender, age)

                    val childUpdates = HashMap<String, Any>()
                    childUpdates[adminId] = admin

                    database.child("admin").updateChildren(childUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Added Successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Failed", it)
                        }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        private const val TAG = "AdminAddNewAdminActivity"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminAddAdmin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}