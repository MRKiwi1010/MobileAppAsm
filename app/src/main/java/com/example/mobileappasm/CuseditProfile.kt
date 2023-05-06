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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.example.mobileappasm.databinding.FragmentAdminAddAdminBinding
import com.example.mobileappasm.databinding.FragmentCusEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class CuseditProfile : Fragment() {

    private lateinit var signup_name: EditText
    private lateinit var signup_email: EditText
    private lateinit var signup_username: EditText
    private lateinit var signup_password: EditText
    private lateinit var adminImageView: ImageView
    private lateinit var signup_button: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentCusEditProfileBinding
    private var selectedImageUri: Uri? = null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        val view = inflater.inflate(R.layout.fragment_cus_edit_profile, container, false)
//        signup_name = view.findViewById(R.id.signup_name)
//        signup_email = view.findViewById(R.id.signup_email)
//        signup_username = view.findViewById(R.id.signup_username)
//        signup_password = view.findViewById(R.id.signup_password)
//        signup_button = view.findViewById(R.id.signup_button)

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")
        val query: Query = databaseReference.orderByChild("username").equalTo(customerUsername)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.children.first()
                    val userimg = user.child("userimg").value.toString()
                    val username = user.child("username").value.toString()
                    val email = user.child("email").value.toString()
                    val name = user.child("name").value.toString()
                    val password = user.child("password").value.toString()

                    // Set the EditText fields with the current user information
                    signup_name.setText(name)
                    signup_email.setText(email)
                    signup_password.setText(password)
                    signup_username.setText(username)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        signup_button.setOnClickListener {
            // Get the new values for the user
            val newName = signup_name.text.toString()
            val newEmail = signup_email.text.toString()
            val newPassword = signup_password.text.toString()
            val newUsername = signup_username.text.toString()

            // Create a HashMap to store the new user information
            val userUpdates = HashMap<String, Any>()
            userUpdates["name"] = newName
            userUpdates["email"] = newEmail
            userUpdates["password"] = newPassword
            userUpdates["username"] = newUsername

            // Update the user with the new information
            databaseReference.child(customerUsername).updateChildren(userUpdates)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT)
                        .show()
                }
            view.findNavController().navigate(R.id.cusProfile)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Update Profile"

        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
//        val imageView = view.findViewById<ImageView>(R.id.adminImageView)

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

}

        // Check if the CAMERA permission has been granted


//        btnSelectImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            pickImageLauncher.launch(intent)
//        }



//
//        val btnAddAdmin = binding.btnAddAdmin
//        var adminId = ""
//        btnAddAdmin.setOnClickListener {
//            database.child("admin").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    var adminCount = 1
//                    for (adminSnapshot in snapshot.children) {
//                        adminId = adminSnapshot.key.toString()
//                        val currentCount = adminId.substring(5).toInt()
//                        if (currentCount >= adminCount) {
//                            adminCount = currentCount + 1
//                        }}
//                    adminId = "admin" + "%03d".format(adminCount)
//
//                    val name = binding.adminName.text.toString()
//                    val email = binding.adminEmail.text.toString()
//                    val username = binding.adminUsername.text.toString()
//                    val password = binding.adminPassword.text.toString()
//                    val contact = binding.adminContact.text.toString()
//                    val gender = when(binding.adminGender.checkedRadioButtonId) {
//                        R.id.maleBtn -> "Male"
//                        R.id.femaleBtn -> "Female"
//                        else -> ""
//                    }
//                    val age = binding.adminAge.text.toString()
//                    val position = "Staff"
//
//                    // Check if all fields are filled
//                    if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || contact.isEmpty() || gender.isEmpty() || age.isEmpty()) {
//                        Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//
//                    // Check if password is at least 8 characters
//                    if (password.length < 8) {
//                        Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//
//                    // Check if contact is in Malaysia contact format
//                    val contactRegex = "^(01)[0-46-9]-*[0-9]{7,8}\$"
//                    if (!contact.matches(contactRegex.toRegex())) {
//                        Toast.makeText(requireContext(), "Contact number must be in Malaysia contact format", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//
//                    // Check if age is numeric
//                    if (!age.matches("[0-9]+".toRegex())) {
//                        Toast.makeText(requireContext(), "Age must be a number", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//
//                    // Check if email is in correct format
//                    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
//                    if (!email.matches(emailRegex.toRegex())) {
//                        Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//
//                    // Check if email or username already exists
//                    var isEmailUsed = false
//                    var isUsernameUsed = false
//                    for (adminSnapshot in snapshot.children) {
//                        val admin = adminSnapshot.getValue(Admin::class.java)
//                        if (admin?.email == email) {
//                            isEmailUsed = true
//                        }
//                        if (admin?.username == username) {
//                            isUsernameUsed = true
//                        }
//                    }
//
//                    if (isEmailUsed) {
//                        Toast.makeText(requireContext(), "Email already in use", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                    if (isUsernameUsed) {
//                        Toast.makeText(requireContext(), "Username already in use", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//
//                    val admin = Admin(selectedImageUri.toString(), name, email, username, password, contact, gender, age, position)
//
//                    //upload image
//                    if (selectedImageUri != null) {
//                        val storageRef = FirebaseStorage.getInstance().reference
//                        val imageRef = storageRef.child("admin/$adminId.jpg")
//
//                        val uploadTask = imageRef.putFile(selectedImageUri!!)
//                        uploadTask.continueWithTask { task ->
//                            if (!task.isSuccessful) {
//                                task.exception?.let { throw it }
//                            }
//                            imageRef.downloadUrl
//                        }.addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                val downloadUri = task.result.toString()
//
//                                admin.imageUri = downloadUri // Save the download URL with the admin data
//                                saveAdminData(admin, adminId)
//
//                                val childUpdates = HashMap<String, Any>()
//                                childUpdates[adminId] = admin
//
//                                database.child("admin").updateChildren(childUpdates)
//                                    .addOnSuccessListener {
//                                        Toast.makeText(requireContext(), "Added Successfully!", Toast.LENGTH_SHORT).show()
//                                        findNavController().navigate(R.id.adminViewAdminList)
//                                    }
//                                    .addOnFailureListener{
//                                        Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
//                                        Log.e(AdminAddAdmin.TAG, "Failed", it)
//                                    }
//                            }
//                        }
//                    }
////                    else if(selectedImage != null)
////                    {
////
////                    }
//                    else
//                    {
//                        Toast.makeText(requireContext(), "Please Select Image!!!", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                }
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }

//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
//            selectedImageUri = data.data
//            binding.adminImageView.setImageURI(selectedImageUri)
//        } else if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
//            val imageBitmap = data.extras?.get("data") as Bitmap
//            selectedImageUri = getImageUri(imageBitmap)
//            binding.adminImageView.setImageBitmap(imageBitmap)
//        }
//    }
//
//    private fun getImageUri(inImage: Bitmap): Uri? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, inImage, "Title", null)
//        return Uri.parse(path)
//    }
//
//    fun saveAdminData(admin: Admin, adminId: String) {
//        // Your code to save the admin data
//    }
//
//
//}
