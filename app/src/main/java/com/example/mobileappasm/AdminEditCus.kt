package com.example.mobileappasm

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.AdminViewChildDetails.Companion.IMAGE_PICK_REQUEST_CODE
import com.example.mobileappasm.databinding.FragmentAdminEditCusBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class AdminEditCus : Fragment() {

    private lateinit var binding: FragmentAdminEditCusBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var customerUsername: String
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminEditCusBinding.inflate(inflater, container, false)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.black)))


        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Customer Details"

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
        customerUsername = arguments?.getString("username") ?: ""
        fetchCustomerData()
//        binding.btnSelectImage.setOnClickListener {
//            selectImage()
//        }
//        binding.btnSaveChanges.setOnClickListener {
//            saveChanges()
//        }
    }

    private fun fetchCustomerData() {
        val databaseReference = firebaseDatabase.getReference("users")
        val query = databaseReference.orderByChild("username").equalTo(customerUsername)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val customer = dataSnapshot.children.first().getValue(Customer::class.java)
                    binding.cusName.setText(customer?.name)
                    binding.cusEmail.setText(customer?.email)
                    binding.cusUsername.setText(customer?.username)
                    binding.cusPassword.setText(customer?.password)

                    val imageView = view?.findViewById<CircleImageView>(R.id.cusImageView)
                    if (customer != null) {
                        customer.userimg?.let { userimg ->
                            if (imageView != null) {
                                Glide.with(requireContext()).load(userimg).into(imageView)
                            }
                        }
                    }
//                    Glide.with(requireContext()).load(customer?.userimg).into(binding.cusImageView)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(requireContext()).load(selectedImageUri).into(binding.cusImageView)
        }
    }

//    private fun saveChanges() {
//        val cusName = binding.cusName.text.toString();
//        val cusEmail = binding.cusEmail.text.toString();
//        val cusUsername = binding.cusUsername.text.toString();
//        val cusPassword = binding.cusPassword.text.toString();
//
//        if (cusName.isEmpty() || cusEmail.isEmpty() || cusUsername.isEmpty() || cusPassword.isEmpty()) {
//            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (cusPassword.length < 8) {
//            Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
//        if (!cusEmail.matches(emailRegex.toRegex())) {
//            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val databaseReference = firebaseDatabase.getReference("users")
//        val query = databaseReference.orderByChild("username").equalTo(customerUsername)
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    dataSnapshot.children.first().ref.child("name")
//                        .setValue(binding.cusName.text.toString())
//                    dataSnapshot.children.first().ref.child("email")
//                        .setValue(binding.cusEmail.text.toString())
//                    dataSnapshot.children.first().ref.child("username")
//                        .setValue(binding.cusUsername.text.toString())
//                    dataSnapshot.children.first().ref.child("password")
//                        .setValue(binding.cusPassword.text.toString())
//                    uploadImageToFirebaseStorage()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun uploadImageToFirebaseStorage() {
//        if (selectedImageUri == null) {
//            Toast.makeText(requireContext(), "Please Select Image!!!", Toast.LENGTH_SHORT).show()
//            saveChangesToDatabase()
//            return
//        }
//        val progressDialog = ProgressDialog(requireContext())
//        progressDialog.setTitle("Uploading Image...")
//        progressDialog.show()
//
//        // Get a reference to the Firebase Storage and create a new file in the "images" directory
//        val storageReference =
//            FirebaseStorage.getInstance().getReference("images/${customerUsername}.jpg")
//        storageReference.putFile(selectedImageUri!!)
//            .addOnSuccessListener {
//                // If the image upload is successful, get the download URL and save it to the database
//                storageReference.downloadUrl.addOnSuccessListener { uri ->
//                    val databaseReference = firebaseDatabase.getReference("users")
//                    val query = databaseReference.orderByChild("username").equalTo(customerUsername)
//                    query.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                dataSnapshot.children.first().ref.child("imageUrl")
//                                    .setValue(uri.toString())
//                                saveChangesToDatabase()
//                            }
//                        }
//
//                        override fun onCancelled(databaseError: DatabaseError) {
//                            Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    })
//                }
//                progressDialog.dismiss()
//            }
//            .addOnFailureListener {
//                // If the image upload fails, display an error message
//                Toast.makeText(context, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT)
//                    .show()
//                progressDialog.dismiss()
//            }
//    }
//
//    private fun saveChangesToDatabase() {
//        // Display a success message and exit the fragment
//        Toast.makeText(context, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
//        requireActivity().supportFragmentManager.popBackStack()
//    }

    companion object {
        const val IMAGE_PICK_REQUEST_CODE = 100
    }
}
