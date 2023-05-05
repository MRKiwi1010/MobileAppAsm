package com.example.mobileappasm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mobileappasm.databinding.FragmentAdminViewDonationDetailsBinding
import com.google.firebase.database.*

class AdminViewDonationDetails : Fragment() {
    private lateinit var binding: FragmentAdminViewDonationDetailsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var username: String
    private lateinit var donation: Donation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminViewDonationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        username = arguments?.getString("username") ?: ""
        fetchCustomerData()
    }

    private fun fetchCustomerData() {
        val databaseReference = firebaseDatabase.getReference("payment")
        val query = databaseReference.orderByChild("username").equalTo(username)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    donation = dataSnapshot.children.first().getValue(Donation::class.java)!!
                    binding.donationUsernameTextView.setText(donation.username)
                    binding.donationChildNameTextView.setText(donation.childName)
                    binding.donationAmountTextView.setText(donation.amount.toString())
                    binding.donationDateTextView.setText(donation.date)
                    binding.donationTimeTextView.setText(donation.time)
                    binding.donationBankTypeTextView.setText(donation.bankType)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}