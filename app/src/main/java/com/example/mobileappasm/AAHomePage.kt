package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class AAHomePage : Fragment() {

    private lateinit var buttonget: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_a_a_home_page, container, false)

//        buttonget = view.findViewById(R.id.buttonget)
//
//        buttonget.setOnClickListener {s
//            view.findNavController().navigate(R.id.cusLoginPage)
//
//        }


        return view
    }


}