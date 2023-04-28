package com.example.mobileappasm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.ItemsAdapter
import com.example.mobileappasm.Domain.ItemsDomain
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CusMainPage : Fragment() {

    private lateinit var recyclerViewPopular: RecyclerView
    private lateinit var recyclerViewNew: RecyclerView
    private lateinit var adapterPopular: RecyclerView.Adapter<*>
    private lateinit var adapterNew: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_main_page, container, false)

        recyclerViewPopular = view.findViewById(R.id.viewPupolar)
        recyclerViewNew = view.findViewById(R.id.viewNew)
        recyclerViewPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNew.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

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
                // Handle error
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
