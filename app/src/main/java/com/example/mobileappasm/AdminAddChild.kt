package com.example.mobileappasm

//import com.example.mobileappasm
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.databinding.FragmentAdminAddChildBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminAddChild : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentAdminAddChildBinding
    private var selectedImageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.childImageView.setImageURI(selectedImageUri)
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

        binding = FragmentAdminAddChildBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Add Child"

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
//        val btnSelectImage = binding.btnSelectImage
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val imageView = view.findViewById<ImageView>(R.id.childImageView)

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        val asiaCountries = arrayOf("China", "India", "Japan", "Indonesia", "Vietnam", "Philippines", "Thailand", "Pakistan", "Bangladesh", "South Korea")
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.child_drop_drop_item, asiaCountries)
        adapter.setDropDownViewResource(R.layout.simple_child_drop_drop_item)
        binding.childNationSpinner.adapter = adapter

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val btnAddChild = binding.btnAddChild
        btnAddChild.setOnClickListener {
            database.child("child").orderByKey().limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var childCount = 1
                    for (childSnapshot in snapshot.children) {
                        val childId = childSnapshot.key.toString()
                        childCount = childId.substring(5).toInt() + 1
                    }
                    val childId = "child" + "%03d".format(childCount)

                    val name = binding.childName.text.toString()
                    val nation = binding.childNationSpinner.selectedItem as String
                    val age = binding.childAge.text.toString()
                    val desc = binding.childDesc.text.toString()
                    val target = binding.childTarget.text.toString()
                    val totalReceived = 0;
                    val child = Child(
                        selectedImageUri.toString(),
                        name,
                        nation,
                        age.toInt(),
                        desc,
                        target.toDouble(),
                        totalReceived.toDouble()
                    )

                    //upload image
                    if (selectedImageUri != null) {
                        val storageRef = FirebaseStorage.getInstance().reference
                        val imageRef = storageRef.child("child_img/$childId.jpg")

                        val uploadTask = imageRef.putFile(selectedImageUri!!)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let { throw it }
                            }
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result.toString()

                                child.childUrl = downloadUri // Save the download URL with the child data
                                saveChildData(child, childId)

                                val childUpdates = HashMap<String, Any>()
                                childUpdates[childId] = child

                                database.child("child").updateChildren(childUpdates)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Added Successfully!", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.adminChildList)
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

    fun saveChildData(child: Child, childId: String) {
        // Your code to save the child data
    }

    companion object {
        private const val TAG = "AdminAddChild"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminAddChild().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
