package com.example.mobileappasm
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var amountRaised: TextView
private lateinit var btnPayment: Button

class CusTotalDonation : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_cus_total_donation, container, false)

        amountRaised = view.findViewById(R.id.amountRaised)
        btnPayment = view.findViewById(R.id.btnPayment)

        val database = FirebaseDatabase.getInstance().reference
        var totalAmountRaised = 0
        database.child("child").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val totalReceived = childSnapshot.child("totalReceived").getValue(Int::class.java) ?: 0
                    totalAmountRaised += totalReceived
                }

                // Update the UI with the total amount raised
                amountRaised.text = "RM " + totalAmountRaised.toString()+".00"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        btnPayment.setOnClickListener { view.findNavController().navigate(R.id.cusDonatePersonalDetails) }

        return view
    }

}