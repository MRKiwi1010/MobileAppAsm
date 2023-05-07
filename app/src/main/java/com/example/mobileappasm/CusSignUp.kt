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
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.databinding.FragmentCusSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class CusSignUp : Fragment() {
    private lateinit var signupName: EditText
    private lateinit var signupUsername: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText

    private lateinit var signupButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var database: DatabaseReference
    private lateinit var reference: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCusSignUpBinding
    private var selectedImageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.adminImageView.setImageURI(selectedImageUri)
        }
    }
    private val MY_PERMISSIONS_REQUEST_CAMERA = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCusSignUpBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Sign Up"

        setHasOptionsMenu(true)

//
//        signupName = view.findViewById(R.id.signup_name)
//        signupEmail = view.findViewById(R.id.signup_email)
//        signupUsername = view.findViewById(R.id.signup_username)
//        signupPassword = view.findViewById(R.id.signup_password)

//        signupButton = view.findViewById(R.id.signup_button)

//
//
//        signupButton.setOnClickListener {
//            if (!validatename() or !validateemail() or !validateUsername() or !validatePassword())  {
//                checkUser()
//            } else {
//                addUser()
//            }
//
//        }
//
//        loginRedirectText.setOnClickListener {
//            view.findNavController().navigate(R.id.cusLoginPage)
//
//        }
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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
//        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val imageView = view.findViewById<CircleImageView>(R.id.adminImageView)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val loginRedirectText = view.findViewById<TextView>(R.id.loginRedirectText)
        loginRedirectText.setOnClickListener { view.findNavController().navigate(R.id.cusLoginPage) }

        // Check if the CAMERA permission has been granted
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


        val signup_button = view.findViewById<Button>(R.id.signup_button)
        var userid = ""
        signup_button.setOnClickListener {
            if (checkBox.isChecked) {

                database.child("users").orderByKey().limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var usersCount = 1
                            for (cusSnapshot in snapshot.children) {
                                userid = cusSnapshot.key.toString()
                                val currentCount = userid.substring(5).toInt()
                                if (currentCount >= usersCount) {
                                    usersCount = currentCount + 1
                                }
                            }
                            userid = "users" + "%03d".format(usersCount)

                            val name = view.findViewById<EditText>(R.id.signup_name).text.toString()
                            val email = view.findViewById<EditText>(R.id.signup_email).text.toString()
                            val username = view.findViewById<EditText>(R.id.signup_password1).text.toString()
                            val password = view.findViewById<EditText>(R.id.signup_password2).text.toString()
                            // Check if all fields are filled
                            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
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

                            // Check if email or username already exists
                            var isEmailUsed = false
                            var isUsernameUsed = false
                            for (adminSnapshot in snapshot.children) {
                                val admin = adminSnapshot.getValue(Customer::class.java)
                                if (admin?.email == email) {
                                    isEmailUsed = true
                                }
                                if (admin?.username == username) {
                                    isUsernameUsed = true
                                }
                            }

                            if (isEmailUsed) {
                                Toast.makeText(
                                    requireContext(),
                                    "Email already in use",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                            if (isUsernameUsed) {
                                Toast.makeText(
                                    requireContext(),
                                    "Username already in use",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }

                            val customer = Customer(
                                email,
                                name,
                                password,
                                username,
                                selectedImageUri.toString()
                            )

                            //upload image
                            if (selectedImageUri != null) {
                                val storageRef = FirebaseStorage.getInstance().reference
                                val imageRef = storageRef.child("user_img/$userid.jpg")

                                val uploadTask = imageRef.putFile(selectedImageUri!!)
                                uploadTask.continueWithTask { task ->
                                    if (!task.isSuccessful) {
                                        task.exception?.let { throw it }
                                    }
                                    imageRef.downloadUrl
                                }.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val downloadUri = task.result.toString()

                                        customer.userimg =
                                            downloadUri // Save the download URL with the admin data
                                        saveAdminData(customer, userid)

                                        val childUpdates = HashMap<String, Any>()
                                        childUpdates[userid] = customer

                                        database.child("users").updateChildren(childUpdates)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Added Successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                findNavController().navigate(R.id.cusLoginPage)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.e(TAG, "Failed", it)
                                            }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please Select Image!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    })
            }else{
                Toast.makeText(requireContext(), "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//
//            if (result.resultCode == RESULT_OK) {
//                val data = result.data
//                if (data?.extras?.containsKey("data") == true) {
//                    val thumbnail: Bitmap = data.extras?.get("data") as Bitmap
//                    binding.adminImageView.setImageBitmap(thumbnail)
//                } else {
//                    selectedImageUri = data?.data
//                    binding.adminImageView.setImageURI(selectedImageUri)
//                }
//            }
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                0 -> {
//                    val selectedImage = data?.extras?.get("data") as Bitmap
//                    binding.adminImageView.setImageBitmap(selectedImage)
//                }
//                1 -> { // Gallery
//                    selectedImageUri = data?.data
//                    binding.adminImageView.setImageURI(selectedImageUri)
//                }
//            }
//        }
//    }

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

    fun saveAdminData(customer: Customer, customerid: String) {
        // Your code to save the admin data
    }

    private fun validatename(): Boolean {
        val name = signupName.text.toString().trim()
        return if (name.isEmpty()) {
            signupName.error = "Full Name cannot be empty"
            false
        } else if (!name.matches("^[a-zA-Z]*$".toRegex())) {
            signupName.error = "Full Name should only contain letters"
            false
        } else {
            signupName.error = null
            true
        }
    }


    private fun validateemail(): Boolean {
        val email = signupEmail.text.toString()
        val emailPattern = Patterns.EMAIL_ADDRESS
        return if (email.isEmpty()) {
            signupEmail.error = "Email cannot be empty"
            false
        } else if (!emailPattern.matcher(email).matches()) {
            signupEmail.error = "Invalid email address"
            false
        } else {
            signupEmail.error = null
            true
        }
    }

    private fun validateUsername(): Boolean {
        val username = signupUsername.text.toString().trim()
        return if (username.isEmpty()) {
            signupUsername.error = "Username cannot be empty"
            false
        } else if (!username.matches("^[a-zA-Z0-9]*$".toRegex())) {
            signupUsername.error = "Username should only contain letters and numbers"
            false
        } else {
            signupUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = signupPassword.text.toString()
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$".toRegex()
        return if (password.isEmpty()) {
            signupPassword.error = "Password cannot be empty"
            false
        } else if (password.contains(" ")) {
            signupPassword.error = "Password cannot contain spaces"
            false
        } else if (!pattern.matches(password)) {
            signupPassword.error = "Password should be at least 8 characters long and contain at least one uppercase and one lowercase letter"
            false
        } else {
            signupPassword.error = null
            true
        }
    }
    private fun checkUser() {
        val userEmail = signupEmail!!.text.toString().trim()
        val userName = signupUsername!!.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("users")

        val checkEmailQuery: Query = reference.orderByChild("email").equalTo(userEmail)
        checkEmailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    signupEmail!!.error = "Email already exists"
                    signupEmail!!.requestFocus()
                } else {
                    val checkUsernameQuery: Query = reference.orderByChild("username").equalTo(userName)
                    checkUsernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                signupUsername!!.error = "Username already exists"
                                signupUsername!!.requestFocus()
                            } else {
                                // User doesn't exist, continue with sign up process
                                // ...
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }



    companion object {
        private const val TAG = "CusSignUp"
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


