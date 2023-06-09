package com.example.mobileappasm

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.databinding.FragmentAdminViewChildDetailsBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class AdminViewChildDetails : Fragment() {
    private lateinit var binding: com.example.mobileappasm.databinding.FragmentAdminViewChildDetailsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var childName: String
    private var selectedImageUri: Uri? = null
    private var childchildnamename = ""
    private var totalReceive = ""
    private lateinit var child: Child
//    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminViewChildDetailsBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Event"
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
        childName = arguments?.getString("childName") ?: ""
        fetchCustomerData()
        setupSpinner()
        binding.childImageView.setOnClickListener {
            selectImage()
        }
        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }
        binding.btnDelete.setOnClickListener {
            deleteChildRecord()
        }
    }

    private fun fetchCustomerData() {

        val databaseReference = firebaseDatabase.getReference("child")
        val query = databaseReference.orderByChild("childName").equalTo(childName)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    child = dataSnapshot.children.first().getValue(Child::class.java)!!
                    binding.childName.setText(child.childName)
                    childchildnamename = child.childName
                    binding.childAge.setText(child.childAge.toString())
                    binding.childDesc.setText(child.child_Des)
                    binding.childTarget.setText(child.target.toString())
                    totalReceive = child.totalReceived.toString()
                    Glide.with(requireContext()).load(child.childUrl).into(binding.childImageView)
                    val spinner: Spinner = binding.spinnerChildNation
                    val adapter = spinner.adapter as ArrayAdapter<String>
                    val position = adapter.getPosition(child.childNation)
                    spinner.setSelection(position)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.spinnerChildNation
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.child_nations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(requireContext()).load(selectedImageUri).into(binding.childImageView)
        }
    }

    private fun saveChanges() {
        val childName = binding.childName.text.toString();
        val childAge = binding.childAge.text.toString();
        val childDesc = binding.childDesc.text.toString();
        val childTarget = binding.childTarget.text.toString();
        val childNation = binding.spinnerChildNation.selectedItem.toString();
        totalReceive

        if(childName.isEmpty() || childAge.isEmpty() || childDesc.isEmpty() || childTarget.isEmpty() || childNation.isEmpty())
        {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (!childAge.matches("[0-9]+".toRegex())) {
            Toast.makeText(requireContext(), "Age must be a number", Toast.LENGTH_SHORT).show()
            return
        }
        val moneyRegex = "^\\$?[0-9]+(\\.[0-9]{1,2})?$" // accepts formats like $10.99 or 100.00
        if (!childTarget.matches(moneyRegex.toRegex())) {
            Toast.makeText(requireContext(), "Invalid money format", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseReference = firebaseDatabase.getReference("child")
        val query = databaseReference.orderByChild("childName").equalTo(childchildnamename)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val childRef = dataSnapshot.children.first().ref

                    childRef.child("childName")
                        .setValue(binding.childName.text.toString())
                    childRef.child("childAge")
                        .setValue(binding.childAge.text.toString().toInt())
                    childRef.child("child_Des")
                        .setValue(binding.childDesc.text.toString())
                    childRef.child("target")
                        .setValue(binding.childTarget.text.toString().toDouble())
                    childRef.child("childNation")
                        .setValue(binding.spinnerChildNation.selectedItem.toString())
                    childRef.child("totalReceived")
                        .setValue(totalReceive.toDouble())
                    uploadImageToFirebaseStorage()
                    view?.findNavController()?.navigate(R.id.adminChildList)
                }
                else
                {
                    Toast.makeText(context, "Data not exists", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteChildRecord() {
        val databaseReference = firebaseDatabase.getReference("child")
        val query = databaseReference.orderByChild("childName").equalTo(childName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.first().ref.removeValue()
                    // Delete the image file from Firebase Storage
                    FirebaseStorage.getInstance().getReference("images/${childName}.jpg").delete()
                    Toast.makeText(context, "Child record deleted successfully", Toast.LENGTH_SHORT).show()
                    // Navigate back to the AdminChildList fragment
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun uploadImageToFirebaseStorage() {
        if (selectedImageUri == null) {
//            Toast.makeText(requireContext(), "Please Select Image!!!", Toast.LENGTH_SHORT).show()
            saveChangesToDatabase()
            return
        }
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading Image...")
        progressDialog.show()

        // Get a reference to the Firebase Storage and create a new file in the "images" directory
        val storageReference =
            FirebaseStorage.getInstance().getReference("images/${childName}.jpg")
        storageReference.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                // If the image upload is successful, get the download URL and save it to the database
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val databaseReference = firebaseDatabase.getReference("child")
                    val query = databaseReference.orderByChild("childName").equalTo(childchildnamename)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataSnapshot.children.first().ref.child("childUrl")
                                    .setValue(uri.toString())
                                saveChangesToDatabase()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }
                progressDialog.dismiss()
            }
            .addOnFailureListener {
                // If the image upload fails, display an error message
                Toast.makeText(context, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
                progressDialog.dismiss()
            }
    }

    private fun saveChangesToDatabase() {
        // Display a success message and exit the fragment
        Toast.makeText(context, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
        view?.findNavController()?.navigate(R.id.adminChildList)
//        requireActivity().supportFragmentManager.popBackStack()

    }

    companion object {
        const val IMAGE_PICK_REQUEST_CODE = 100
    }
}