package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.cusDonationAdapter
import com.example.mobileappasm.data.model.cusViewModel
import com.example.mobileappasm.databinding.FragmentAdminDonationHistoryBinding
import com.google.firebase.database.*

class CusDonationHistory : Fragment() {
    private lateinit var binding: FragmentAdminDonationHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var cusDonationAdapter: cusDonationAdapter
    private lateinit var donationList: ArrayList<Donation>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminDonationHistoryBinding.inflate(inflater, container, false)
        recyclerView = binding.donationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        cusDonationAdapter = cusDonationAdapter(requireContext())
        recyclerView.adapter = cusDonationAdapter
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getCustomerUsername()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("payment")
        val query: Query = databaseReference.orderByChild("username").equalTo(customerUsername)
        donationList = ArrayList()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donationList.clear()
                for (userSnapShot in snapshot.children) {
                    val donation = userSnapShot.getValue(Donation::class.java)
                    if (donation != null) {
                        donationList.add(donation)
                    }
                }
                cusDonationAdapter.submitList(donationList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}
