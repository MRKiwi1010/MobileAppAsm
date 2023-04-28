package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class CusProfile : Fragment() {

    private lateinit var  textView : TextView
    private lateinit var textView2: TextView
    private lateinit var textView54: TextView
    private lateinit var textView56: TextView
    private lateinit var textView58: TextView
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_profile, container, false)

        //name email email name username
        textView = view.findViewById(R.id.textView)
        textView2 = view.findViewById(R.id.textView2)
        textView54 = view.findViewById(R.id.textView54)
        textView56 = view.findViewById(R.id.textView56)
        textView58 = view.findViewById(R.id.textView58)
        imageView = view.findViewById(R.id.imageView)


        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val username = "jason" // replace with your actual users key
        val childReference = databaseReference.child(username)


        childReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get child information from Firebase Realtime Database
                val useremail = dataSnapshot.child("email").value.toString()
                val user_name = dataSnapshot.child("name").value.toString()
                val userpassword = dataSnapshot.child("password").value.toString()
                val username = dataSnapshot.child("username").value.toString()
                val userimg = dataSnapshot.child("userimg").value.toString()

                // Set the retrieved information to the respective TextViews
                textView2.text =  useremail
                textView.text = user_name
                textView54.text = useremail
                textView56.text = user_name
                textView58.text = username
//                //the childimg is imageview
                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(userimg)

                Glide.with(requireContext())
                    .load(storageRef)
                    .into(imageView)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        return view
    }


}