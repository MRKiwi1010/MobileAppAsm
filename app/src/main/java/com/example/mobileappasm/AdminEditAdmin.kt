package com.example.mobileappasm

import android.app.Activity.RESULT_OK
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.databinding.FragmentAdminEditAdminBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminEditAdmin : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAdminEditAdminBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adminUsername: String
    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference

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
        binding = FragmentAdminEditAdminBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Admin"

        setHasOptionsMenu(true)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        adminUsername = arguments?.getString("username") ?: ""
        database = FirebaseDatabase.getInstance().reference
        fetchAdminData()
        binding.btnSelectImage.setOnClickListener {
            selectImage()
        }
        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }
        binding.btnDelete.setOnClickListener{
            deleteAdmin()
        }
    }

    private fun fetchAdminData() {
        val databaseReference = firebaseDatabase.getReference("admin")
        val query = databaseReference.orderByChild("username").equalTo(adminUsername)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val admin = dataSnapshot.children.first().getValue(Admin::class.java)
                    binding.adminUsername.setText(admin?.username)
                    binding.adminEmail.setText(admin?.email)
                    binding.adminAge.setText(admin?.age)
                    binding.adminName.setText(admin?.name)
                    binding.adminContact.setText(admin?.contact)
                    binding.adminPassword.setText(admin?.password)
                    when(admin?.gender) {
                        "Male" -> binding.maleBtn.setChecked(true)
                        "Female" -> binding.femaleBtn.setChecked(true)
                    }
                    val imageView = view?.findViewById<ImageView>(R.id.adminImageView)

                    if (admin != null) {
                        admin.imageUri?.let { imageUri ->
                            if (imageView != null) {
                                Glide.with(requireContext()).load(imageUri).into(imageView)
                            }
                        }
                    }
                    selectedImageUri = admin?.imageUri?.toUri()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun saveChanges() {
        val adminRef = firebaseDatabase.getReference("admin")

        // Get the current admin's username and email
        val currentUsername = binding.adminUsername.text.toString().trim()
        val currentEmail = binding.adminEmail.text.toString().trim()
        var adminId = ""
        database.child("admin").orderByKey().limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (adminSnapshot in snapshot.children) {
                        adminId = adminSnapshot.key.toString()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            })

        // Check if the new email already exists in the database
        adminRef.orderByChild("email").equalTo(currentEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Email already exists in the database, show error message
                        Toast.makeText(requireContext(), "Email already exists", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        // Check if the new username already exists in the database
                        adminRef.orderByChild("username").equalTo(currentUsername)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Username already exists in the database, show error message
                                        Toast.makeText(
                                            requireContext(),
                                            "Username already exists",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val name = binding.adminName.text.toString()
                                        val email = binding.adminEmail.text.toString()
                                        val username = binding.adminUsername.text.toString()
                                        val password = binding.adminPassword.text.toString()
                                        val contact = binding.adminContact.text.toString()
                                        val gender =
                                            when (binding.adminGender.checkedRadioButtonId) {
                                                R.id.maleBtn -> "Male"
                                                R.id.femaleBtn -> "Female"
                                                else -> ""
                                            }
                                        val age = binding.adminAge.text.toString()

                                        // Check if all fields are filled
                                        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || contact.isEmpty() || gender.isEmpty() || age.isEmpty()) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Please fill in all fields",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // Check if password is at least 8 characters
                                        if (password.length < 8) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Password must be at least 8 characters",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // Check if contact is in Malaysia contact format
                                        val contactRegex = "^(01)[0-46-9]-*[0-9]{7,8}\$"
                                        if (!contact.matches(contactRegex.toRegex())) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Contact number must be in Malaysia contact format",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // Check if age is numeric
                                        if (!age.matches("[0-9]+".toRegex())) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Age must be a number",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // Check if email is in correct format
                                        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
                                        if (!email.matches(emailRegex.toRegex())) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Invalid email format",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // Create a new admin object
                                        val admin = Admin(
                                            selectedImageUri.toString(),
                                            name,
                                            email,
                                            username,
                                            password,
                                            contact,
                                            gender,
                                            age,
                                            position = "Staff"
                                        )

                                        if (selectedImageUri != null) {
                                            val storageReference =
                                                FirebaseStorage.getInstance().reference
                                            val imageRef =
                                                storageReference.child("admin_img/$adminId.jpg")

                                            val uploadTask = imageRef.putFile(selectedImageUri!!)
                                            uploadTask.continueWithTask { task ->
                                                if (!task.isSuccessful) {
                                                    task.exception?.let { throw it }
                                                }
                                                imageRef.downloadUrl
                                            }.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val downloadUri = task.result.toString()

                                                    admin.imageUri = downloadUri
                                                    saveAdminData(admin, adminId)

                                                }
                                            }
                                        }
                                        // If the adminId is not empty, update the existing admin with the new admin object
                                        if (adminId.isNotEmpty()) {
                                            adminRef.child(adminId).setValue(admin)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Admin updated successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        findNavController().navigate(R.id.adminViewAdminList)
                                                    } else {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Failed to update admin",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        } else {
                                            // If the adminId is empty, create a new admin with the new admin object
                                            val newAdminRef = adminRef.push()
                                            newAdminRef.setValue(admin)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Admin added successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Failed to add admin",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            })
    }












        private fun deleteAdmin() {
        val adminRef = firebaseDatabase.getReference("admin")
        val query = adminRef.orderByChild("username").equalTo(adminUsername)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val adminKey = dataSnapshot.children.first().key
                    val admin = dataSnapshot.children.first().getValue(Admin::class.java)

                    // Request user confirmation before deleting admin
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirm deletion")
                        .setMessage("Are you sure you want to delete admin ${admin?.name}?")
                        .setPositiveButton("Yes") { _, _ ->
                            adminRef.child(adminKey!!).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Admin deleted successfully", Toast.LENGTH_SHORT).show()
                                    view?.findNavController()?.navigate(R.id.adminViewAdminList)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Failed to delete admin", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error if needed
            }
        })
    }


    fun saveAdminData(admin: Admin, adminId: String) {
        // Your code to save the admin data
    }


    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(this).load(selectedImageUri).into(binding.adminImageView)
        }
    }


    companion object {
        const val REQUEST_IMAGE_PICK = 1
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminEditCus().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}