package com.example.mobileappasm

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.databinding.FragmentAdminAddAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminAddAdmin : Fragment() {

    private var param1: String? = null
    private var param2: String? = null


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentAdminAddAdminBinding
    private var selectedImageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.adminImageView.setImageURI(selectedImageUri)
        }
    }
    private val MY_PERMISSIONS_REQUEST_CAMERA = 123

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
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Add Admin"

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.black)))


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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
//        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val imageView = view.findViewById<CircleImageView>(R.id.adminImageView)

        // Check if the CAMERA permission has been granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // If the permission has not been granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA)
        }

//        btnSelectImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            pickImageLauncher.launch(intent)
//        }

        binding.adminImageView.setOnClickListener {
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


        val btnAddAdmin = binding.btnAddAdmin
        var adminId = ""
        btnAddAdmin.setOnClickListener {
            database.child("admin").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var adminCount = 1
                    for (adminSnapshot in snapshot.children) {
                        adminId = adminSnapshot.key.toString()
                        val currentCount = adminId.substring(5).toInt()
                        if (currentCount >= adminCount) {
                            adminCount = currentCount + 1
                        }}
                    adminId = "admin" + "%03d".format(adminCount)

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
                    val position = "staff"

                    // Check if all fields are filled
                    if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || contact.isEmpty() || gender.isEmpty() || age.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Check if password is at least 8 characters
                    if (password.length < 8) {
                        Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Check if contact is in Malaysia contact format
                    val contactRegex = "^(01)[0-46-9]-*[0-9]{7,8}\$"
                    if (!contact.matches(contactRegex.toRegex())) {
                        Toast.makeText(requireContext(), "Contact number must be in Malaysia contact format", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Check if age is numeric
                    if (!age.matches("[0-9]+".toRegex())) {
                        Toast.makeText(requireContext(), "Age must be a number", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Check if email is in correct format
                    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
                    if (!email.matches(emailRegex.toRegex())) {
                        Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                        return
                    }

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

                    val admin = Admin(selectedImageUri.toString(), name, email, username, password, contact, gender, age, position)

                    //upload image
                    if (selectedImageUri != null) {
                        val storageRef = FirebaseStorage.getInstance().reference
                        val imageRef = storageRef.child("admin/$adminId.jpg")

                        val uploadTask = imageRef.putFile(selectedImageUri!!)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let { throw it }
                            }
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result.toString()

                                admin.imageUri = downloadUri // Save the download URL with the admin data
                                saveAdminData(admin, adminId)

                                val childUpdates = HashMap<String, Any>()
                                childUpdates[adminId] = admin

                                database.child("admin").updateChildren(childUpdates)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Added Successfully!", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.adminViewAdminList)
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                                        Log.e(TAG, "Failed", it)
                                    }
                            }
                        }
                    }
//                    else if(selectedImage != null)
//                    {
//
//                    }
                    else
                    {
                        Toast.makeText(requireContext(), "Please Select Image!!!", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                }
            })
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

        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            selectedImageUri = data.data
            binding.adminImageView.setImageURI(selectedImageUri)
        } else if (resultCode == RESULT_OK && requestCode == 0 && data != null) {
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

    fun saveAdminData(admin: Admin, adminId: String) {
        // Your code to save the admin data
    }

    companion object {
        private const val TAG = "AdminAddAdmin"
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