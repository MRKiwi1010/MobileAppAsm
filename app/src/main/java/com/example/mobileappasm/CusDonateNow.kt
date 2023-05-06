package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController

class CusDonateNow : Fragment() {

    private lateinit var editAmount: EditText
    private lateinit var btnPayment: Button
    private lateinit var radioButton:CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_donate_now, container, false)
        editAmount = view.findViewById(R.id.editAmount)
        btnPayment = view.findViewById(R.id.btnPayment)
        radioButton = view.findViewById(R.id.radioButton)

        btnPayment.setOnClickListener {
            val amount = editAmount.text.toString()
            if (radioButton.isChecked) {
                // Pass the payment amount to the cusPaymentGateway fragment
                val bundle = Bundle()
                bundle.putString("paymentAmount", amount)
                findNavController().navigate(R.id.cusPaymentGateway, bundle)
            }
            else{
                Toast.makeText(requireContext(), "Please accept the conditions", Toast.LENGTH_SHORT).show()
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Donate"

        setHasOptionsMenu(true)

        return view
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


}
