package com.example.mobileappasm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobileappasm.Adapter.ItemsAdapter
import com.example.mobileappasm.Domain.ItemsDomain
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class CusMainPage : Fragment() {

    private lateinit var recyclerViewPopular: RecyclerView
    private lateinit var recyclerViewNew: RecyclerView
    private lateinit var adapterPopular: RecyclerView.Adapter<*>
    private lateinit var adapterNew: RecyclerView.Adapter<*>
    private lateinit var textView4 : TextView
    private lateinit var imageView2: ImageView
    private lateinit var textView71 : TextView
    private lateinit var textView73 : TextView
    private lateinit var textView52: TextView
    private lateinit var textView54: TextView
    private lateinit var textView56: TextView
    private lateinit var textView58: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_main_page, container, false)

        recyclerViewPopular = view.findViewById(R.id.viewPupolar)
        recyclerViewNew = view.findViewById(R.id.viewNew)
        recyclerViewPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNew.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        textView4 = view.findViewById(R.id.textView4)
        imageView2 = view.findViewById(R.id.imageView2)
        textView71 = view.findViewById(R.id.textView71)
        textView73= view.findViewById(R.id.textView73)
        // this is the child text view
        textView52 = view.findViewById(R.id.textView52)
// donate now
        textView54 = view.findViewById(R.id.textView54)
// about us
        textView56 = view.findViewById(R.id.textView56)
// history
        textView58 = view.findViewById(R.id.textView58)


        textView71.setOnClickListener {
            view.findNavController().navigate(R.id.cusDonatePersonalDetails)
        }
        textView73.setOnClickListener {
            view.findNavController().navigate(R.id.cusDonatePersonalDetails)
        }

        textView52.setOnClickListener {
            view.findNavController().navigate(R.id.cusViewChild)
        }
        textView54.setOnClickListener {
            view.findNavController().navigate(R.id.cusDonateNow)
        }
        textView56.setOnClickListener {
            view.findNavController().navigate(R.id.cusAboutUs)
        }
        textView58.setOnClickListener {
            view.findNavController().navigate(R.id.cusDonationHistory)
        }


        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val username = customerUsername // replace with your actual users key
        val childReference = databaseReference.child(username)

        childReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get child information from Firebase Realtime Database
                val user_name = dataSnapshot.child("name").value.toString()
                val userimg = dataSnapshot.child("userimg").value.toString()
                textView4.text = user_name

                Glide.with(requireContext()).load(userimg).into(imageView2)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        val itemsArraylist = ArrayList<ItemsDomain>()
        val itemsArraylist2 = ArrayList<ItemsDomain>()
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child("child").orderByKey()

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Loop through the data and add it to the itemsArraylist
                for (childSnapshot in dataSnapshot.children) {
//                    val childAge = childSnapshot.child("childAge").getValue(Int::class.java)
                    val childName = childSnapshot.child("childName").getValue(String::class.java)
                    val childNation = childSnapshot.child("childNation").getValue(String::class.java)
//                    val child_Des = childSnapshot.child("child_Des").getValue(String::class.java)
                    val target = childSnapshot.child("target").getValue(Int::class.java)?.toInt() ?: 0
                    val  childimg= childSnapshot.child("childUrl").getValue(String::class.java)
                    val totalReceived = childSnapshot.child("totalReceived").getValue(Int::class.java)?.toInt() ?: 0

//                    val item = ItemsDomain(childName!!, childNation!!,"",totalReceived!!,0, childimg!!,0 )
//                    itemsArraylist.add(item)
//                    itemsArraylist2.add(item)

                    if (target < totalReceived) {
                        val item = ItemsDomain(childName!!, childNation!!, "", totalReceived!!, target!!, childimg!!, 0)
                        itemsArraylist.add(item)
                    }
                    else  if (target > totalReceived) {
                        val item = ItemsDomain(childName!!, childNation!!, "", totalReceived!!, target!!, childimg!!, 0)
                        itemsArraylist2.add(item)
                    }
                }

                // Update the adapter with the new data
                adapterNew.notifyDataSetChanged()
                adapterPopular.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        adapterNew = ItemsAdapter(itemsArraylist)
        adapterPopular = ItemsAdapter(itemsArraylist2)

        recyclerViewNew.adapter = adapterNew
        recyclerViewPopular.adapter = adapterPopular

        adapterNew.notifyDataSetChanged()
        adapterPopular.notifyDataSetChanged()

        return view
    }
}
