package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class CusProfile : Fragment() {

    private lateinit var  textView : TextView
    private lateinit var textView2: TextView
    private lateinit var textView54: TextView
    private lateinit var textView56: TextView
    private lateinit var textView58: TextView
    private lateinit var imageView: ImageView
    private lateinit var textView52: TextView
    private lateinit var button: Button


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
        textView52 = view.findViewById(R.id.textView52)
        button = view.findViewById(R.id.button)
        val paymentNavi = view.findViewById<LinearLayout>(R.id.paymentNavi)
        val locationNavi = view.findViewById<LinearLayout>(R.id.locationNavi)
        val historyNavi = view.findViewById<LinearLayout>(R.id.historyNavi)

//        paymentNavi.setOnClickListener{view.findNavController().navigate(R.id.cus)}
        textView52.setOnClickListener{ view.findNavController().navigate(R.id.cuseditProfile) }
        button.setOnClickListener { view.findNavController().navigate(R.id.cuseditProfile) }
        paymentNavi.setOnClickListener { view.findNavController().navigate(R.id.cusDonateNow) }
        locationNavi.setOnClickListener { view.findNavController().navigate(R.id.cusAboutUs) }
        historyNavi.setOnClickListener { view.findNavController().navigate(R.id.cusDonationHistory) }

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val username = customerUsername // replace with your actual users key

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val query: Query = databaseReference.orderByChild("username").equalTo(username)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val username = childSnapshot.child("username").value.toString()
                    val email = childSnapshot.child("email").value.toString()
                    val name = childSnapshot.child("name").value.toString()
                    val password = childSnapshot.child("childNation").value.toString()
                    val userimg = childSnapshot.child("userimg").value.toString()

                    textView.text = username
                    textView2.text = email
                    textView54.text = email
                    textView56.text = name
                    textView58.text = username

                    Glide.with(requireContext()).load(userimg).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return view
    }


}