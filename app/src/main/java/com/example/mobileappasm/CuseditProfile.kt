package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.database.*

class CuseditProfile : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_edit_profile, container, false)

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val username = customerUsername // replace with your actual users key

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val query: Query = databaseReference.orderByChild("username").equalTo(username)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val userimg = childSnapshot.child("userimg").value.toString()
                    val username = childSnapshot.child("username").value.toString()
                    val email = childSnapshot.child("email").value.toString()
                    val name = childSnapshot.child("name").value.toString()
                    val password = childSnapshot.child("childNation").value.toString()
//
//
//                    textView.text = username
//                    textView2.text = email
//                    textView54.text = email
//                    textView56.text = name
//                    textView58.text = username
//
//                    Glide.with(requireContext()).load(userimg).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })



        return view
    }


}