package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CusDonateNow : Fragment() {

    val players = arrayOf("RM 10","RM 10","RM 10","RM 10","RM 10","RM 10","RM 10","RM 10","RM 10","RM 10","RM 10",)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_donate_now, container, false)




        return view
    }


}