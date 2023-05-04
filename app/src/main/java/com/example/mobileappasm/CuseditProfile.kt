package com.example.mobileappasm
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.database.*
import java.util.*

class CuseditProfile : Fragment() {

    private lateinit var signup_name: EditText
    private lateinit var signup_email: EditText
    private lateinit var signup_username: EditText
    private lateinit var signup_password: EditText
    private lateinit var imageView12: ImageView
    private lateinit var signup_button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_edit_profile, container, false)
        signup_name = view.findViewById(R.id.signup_name)
        signup_email = view.findViewById(R.id.signup_email)
        signup_username = view.findViewById(R.id.signup_username)
        signup_password = view.findViewById(R.id.signup_password)
        imageView12 = view.findViewById(R.id.imageView12)
        signup_button = view.findViewById(R.id.signup_button)

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
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
                    Glide.with(requireContext()).load(userimg).into(imageView12)
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
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            view.findNavController().navigate(R.id.cusProfile)
        }
        return view
    }
}
