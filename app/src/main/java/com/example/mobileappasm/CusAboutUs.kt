package com.example.mobileappasm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class CusAboutUs : Fragment() {
    private lateinit var textView6: TextView
    private lateinit var textView62 : TextView
    private lateinit var textView7: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_cus_about_us, container, false)

        textView6 = view.findViewById(R.id.textView6)
        textView7 = view.findViewById(R.id.textView7)
        textView62 = view.findViewById(R.id.textView62)

        textView6.setOnClickListener { dialPhoneNumber("60189481671") }
        textView7.setOnClickListener { dialPhoneNumber("60173970217") }
        textView62.setOnClickListener { sendEmail("andyhzy-pm20@student.tarc.edu.my") }

        return view
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(dialIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to dial phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(emailAddress: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$emailAddress")
        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to send email", Toast.LENGTH_SHORT).show()
        }
    }
}
