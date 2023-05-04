package com.example.mobileappasm
//
//import android.app.Activity.RESULT_OK
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.bumptech.glide.Glide
//import com.example.mobileappasm.databinding.FragmentAdminEditAdminBinding
//import com.google.firebase.database.*
//
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
//class AdminEditAdmin : Fragment() {
//    private var param1: String? = null
//    private var param2: String? = null
//    private lateinit var binding: FragmentAdminEditAdminBinding
//    private lateinit var firebaseDatabase: FirebaseDatabase
//    private lateinit var adminUsername: String
//    private var selectedImageUri: Uri? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentAdminEditAdminBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        firebaseDatabase = FirebaseDatabase.getInstance()
//        adminUsername = arguments?.getString("username") ?: ""
//        fetchCustomerData()
//        binding.btnSelectImage.setOnClickListener {
//            selectImage()
//        }
//        binding.btnSaveChanges.setOnClickListener {
//            saveChanges()
//        }
//    }
//
//    private fun fetchCustomerData() {
//        val databaseReference = firebaseDatabase.getReference("admin")
//        val query = databaseReference.orderByChild("username").equalTo(customerUsername)
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val customer = dataSnapshot.children.first().getValue(Customer::class.java)
//                    binding.adminName.setText(customer?.name)
//                    binding.adminAge.setText(customer?.age.toString())
//                    binding.adminEmail.setText(customer?.email)
//                    binding.adminUsername.setText(customer?.username)
//                    binding.adminPassword.setText(customer?.password)
//                    binding.adminContact.setText(customer?.contact)
//                    when (customer?.gender) {
//                        "Male" -> binding.maleBtn.isChecked = true
//                        "Female" -> binding.femaleBtn.isChecked = true
//                        "Other" -> binding.otherRadioButton.isChecked = true
//                    }
//                    Glide.with(requireContext())
//                        .load(customer?.userimg)
//                        .into(binding.adminImageView)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle database error if needed
//            }
//        })
//    }
//
//    private fun selectImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, 1000)
//    }
//
//    private fun saveChanges() {
//        val databaseReference = firebaseDatabase.getReference("users")
//        val query = databaseReference.orderByChild("username").equalTo(customerUsername)
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val customer = dataSnapshot.children.first().getValue(Customer::class.java)
//                    customer?.name = binding.adminName.text.toString()
//                    customer?.age = binding.adminAge.text.toString().toInt()
//                    customer?.email = binding.adminEmail.text.toString()
//                    customer?.username = binding.adminUsername.text.toString()
//                    customer?.password = binding.adminPassword.text.toString()
//                    customer?.contact = binding.adminContact.text.toString()
//                    when (binding.adminGender.checkedRadioButtonId) {
//                        binding.maleBtn.id -> customer?.gender = "Male"
//                        binding.femaleBtn.id -> customer?.gender = "Female"
//                        binding.otherRadioButton.id -> customer?.gender = "Other"
//                    }
//                    if (selectedImageUri != null) {
//                        uploadImage(customer!!)
//                    } else {
//                        saveCustomerdata(customer!!)
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle database error if needed
//            }
//        })
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            AdminEditCus().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}