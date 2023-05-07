package com.example.mobileappasm
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.example.mobileappasm.databinding.FragmentCusEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CuseditProfile : Fragment() {

    private lateinit var signup_name: EditText
    private lateinit var signup_email: EditText
    private lateinit var signup_password2: EditText
    private lateinit var signup_password1: EditText
    private lateinit var adminImageView: ImageView
    private lateinit var signup_button: Button
    private lateinit var database: DatabaseReference


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCusEditProfileBinding
    private var selectedImageUri: Uri? = null


    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.adminImageView.setImageURI(selectedImageUri)
        }
    }
    private val MY_PERMISSIONS_REQUEST_CAMERA = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCusEditProfileBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Update Profile"
        setHasOptionsMenu(true)
        return binding.root
    }
//        val view = inflater.inflate(R.layout.fragment_cus_edit_profile, container, false)


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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val imageView = view.findViewById<ImageView>(R.id.adminImageView)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // If the permission has not been granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA)
        }

        imageView.setOnClickListener {
            val options = mutableListOf<CharSequence>("Choose from Gallery", "Cancel")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                options.add(0, "Take Photo")
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Add Photo!")
            builder.setItems(options.toTypedArray()) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, 0)
                    }
                    options[item] == "Choose from Gallery" -> {
                        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, 1)
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername1 = viewModel.getCustomerUsername()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val query: Query = databaseReference.orderByChild("username").equalTo(customerUsername1)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.children.first()
                    val userimg = user.child("userimg").value.toString()
                    val email = user.child("email").value.toString()
                    val name = user.child("name").value.toString()
                    val password = user.child("password").value.toString()

                    signup_name = view.findViewById(R.id.signup_name)
                    signup_email = view.findViewById(R.id.signup_email)
                    signup_password1 = view.findViewById<EditText>(R.id.signup_password1)
                    signup_password2 = view.findViewById<EditText>(R.id.signup_password2)
                    signup_button = view.findViewById(R.id.signup_button)
                    adminImageView = view.findViewById<ImageView>(R.id.adminImageView)
                    // Set the EditText fields with the current user information
                    signup_name.setText(name)
                    signup_email.setText(email)
                    signup_password1.setText(password)
                    signup_password2.setText(password)


                     selectedImageUri = userimg?.toUri()

                    if (userimg.isNotEmpty()) {
                        Glide.with(requireContext()).load(userimg).into(adminImageView)
                    } else {
                        adminImageView.setImageResource(R.drawable.profile)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val signup_button = view.findViewById<Button>(R.id.signup_button)


        signup_button.setOnClickListener {

            val customerUsername = viewModel.getCustomerUsername()
            if (customerUsername.isNotEmpty()) {
                val usersRef = FirebaseDatabase.getInstance().getReference("users")
                val query2 = usersRef.orderByChild("username").equalTo(customerUsername)
                query2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (userSnapshot in dataSnapshot.children) {
                                // Get the new values for the user
                                val newName = signup_name.text.toString()
                                val newEmail = signup_email.text.toString()
                                val newPassword = signup_password1.text.toString()
                                val newPassword2 = signup_password2.text.toString()
                                val username = dataSnapshot.child("username").value.toString()
                                if (newName == username && newEmail == dataSnapshot.child("email").value.toString() &&
                                    newPassword == "" && selectedImageUri == null
                                ) {
                                    Toast.makeText(requireContext(), "No changes to update", Toast.LENGTH_SHORT).show()
                                    return
                                }

                                val customer = Customer(newEmail, newName, newPassword, username, selectedImageUri.toString())

                                if (username.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newPassword2.isEmpty()) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please fill in all fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }
                                if (newPassword != newPassword2) {
                                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                if (newPassword.length < 8) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Password must be at least 8 characters",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }
                                // Check if email is in correct format
                                val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
                                if (!newPassword.matches(emailRegex.toRegex())) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Invalid email format",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }


                                if (selectedImageUri != null) {

                                    val storageRef = FirebaseStorage.getInstance().reference
                                    val imageRef = storageRef.child("user_img/$username.jpg")

                                    val uploadTask = imageRef.putFile(selectedImageUri!!)
                                    uploadTask.continueWithTask { task ->
                                        if (!task.isSuccessful) {
                                            task.exception?.let { throw it }
                                        }
                                        imageRef.downloadUrl
                                    }.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val downloadUri = task.result.toString()

                                            customer.userimg = downloadUri
                                            saveAdminData(customer, username)

                                            // Update the user's information in the database
                                            val updateMap = hashMapOf<String, Any>(
                                                "email" to newEmail,
                                                "name" to newName,
                                                "password" to newPassword,
                                                "userimg" to customer.userimg,
                                                "username" to customerUsername
                                            )

                                            val userRef = databaseReference.child(userSnapshot.key!!)
                                            userRef.setValue(updateMap).addOnSuccessListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Profile updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                view.findNavController().navigate(R.id.cusProfile)
                                            }.addOnFailureListener { e ->
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Profile update failed: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                } else {
                                    // Update the user's information in the database
                                    val updateMap = hashMapOf<String, Any>(
                                        "email" to newEmail,
                                        "name" to newName,
                                        "password" to newPassword,
                                        "userimg" to customer.userimg,
                                        "username" to customerUsername
                                    )
                                    val userRef = databaseReference.child(userSnapshot.key!!)
                                    userRef.setValue(updateMap).addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Profile updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        view.findNavController().navigate(R.id.cusProfile)
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Profile update failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            requireContext(),
                            "Database error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // If the permission has not been granted, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            selectedImageUri = data.data
            binding.adminImageView.setImageURI(selectedImageUri)
        } else if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            selectedImageUri = getImageUri(imageBitmap)
            binding.adminImageView.setImageBitmap(imageBitmap)
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun saveAdminData(customer: Customer, username: String) {
        // Your code to save the admin data
    }
    companion object {
        private const val TAG = "CuseditProfile"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CuseditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
