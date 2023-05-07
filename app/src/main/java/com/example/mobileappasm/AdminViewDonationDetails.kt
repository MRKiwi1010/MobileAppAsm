package com.example.mobileappasm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Donation Details"

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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