package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.CustomerAdapter
import com.example.mobileappasm.Adapter.DonationAdapter
import com.example.mobileappasm.databinding.FragmentAdminCustomerListBinding
import com.example.mobileappasm.databinding.FragmentAdminDonationHistoryBinding
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminDonationHistory : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAdminDonationHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private lateinit var donationList: ArrayList<Donation>

    private lateinit var database: FirebaseDatabase
    private lateinit var donationRef: DatabaseReference

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
        binding = FragmentAdminDonationHistoryBinding.inflate(inflater, container, false)
        recyclerView = binding.donationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        donationAdapter = DonationAdapter(requireContext())
        recyclerView.adapter = donationAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        donationRef = database.getReference("payment")

        donationList = ArrayList()
        donationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donationList.clear()
                for (userSnapShot in snapshot.children) {
                    val donation = userSnapShot.getValue(Donation::class.java)
                    if (donation != null) {
                        donationList.add(donation)
                    }
                }
                donationAdapter.submitList(donationList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminDonationHistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}