package com.example.mobileappasm

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

class cusPaymentDone : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_payment_done, container, false)

        // Use a Handler to delay the navigation after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            view.findNavController().navigate(R.id.cusDonationHistory)
        }, 2000)

        return view
    }
}