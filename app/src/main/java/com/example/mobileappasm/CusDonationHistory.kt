package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

class CusDonationHistory : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_donation_history, container, false)

        val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10")

        // Create an ArrayAdapter using the sample data
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

        // Set the adapter for the ListView
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        // need custom list view


        return view
    }

}
