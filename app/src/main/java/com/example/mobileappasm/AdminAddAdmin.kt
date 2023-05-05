package com.example.mobileappasm

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.databinding.FragmentAdminAddAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


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
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val imageView = view.findViewById<ImageView>(R.id.adminImageView)

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
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
                    val position = "Staff"

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
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
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