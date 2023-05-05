package com.example.mobileappasm

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
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

        buttonget = view.findViewById(R.id.playBtn)

        buttonget.setOnClickListener {
            view.findNavController().navigate(R.id.cusLoginPage)

        }

        view.findViewById<Button>(R.id.playplayBtn).setOnClickListener{
            view.findNavController().navigate(R.id.adminLoginPage)
        }


        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Enable back button callback
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Show confirmation dialog before exiting the app
            AlertDialog.Builder(requireContext())
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes") { _, _ ->
                    requireActivity().finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

}