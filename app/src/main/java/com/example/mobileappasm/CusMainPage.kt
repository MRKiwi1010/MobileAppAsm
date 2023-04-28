package com.example.mobileappasm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

                // Set the retrieved information to the respective TextViews
                textView4.text = user_name
                //textView58.text = username
//                //the childimg is imageview
//                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(userimg)
//
//                Glide.with(requireContext())
//                    .load(storageRef)
//                    .into(imageView)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        val itemsArraylist = ArrayList<ItemsDomain>()
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
//                    val target = childSnapshot.child("target").getValue(Int::class.java)
                    val  childimg= childSnapshot.child("childUrl").getValue(String::class.java)
                    val totalReceived = childSnapshot.child("totalReceived").getValue(Int::class.java)

                    val item = ItemsDomain(childName!!, childNation!!,"",totalReceived!!,0, childimg!!,0 )
                    itemsArraylist.add(item)

//                    if (totalReceived > target) {
//                        val item = ItemsDomain(childName!!, childNation!!, "", totalReceived!!, target!!, childimg!!, 0)
//                        itemsArraylist.add(item)
//                    }
                }

                // Update the adapter with the new data
                adapterNew.notifyDataSetChanged()
                adapterPopular.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        adapterNew = ItemsAdapter(itemsArraylist)
        adapterPopular = ItemsAdapter(itemsArraylist)

        recyclerViewNew.adapter = adapterNew
        recyclerViewPopular.adapter = adapterPopular

        adapterNew.notifyDataSetChanged()
        adapterPopular.notifyDataSetChanged()

        return view
    }
}
