package com.example.mobileappasm.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.R
import com.example.mobileappasm.databinding.FragmentCusSignInGoogleTestBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class cusSignInGoogleTest : Fragment() {

    private lateinit var binding: FragmentCusSignInGoogleTestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCusSignInGoogleTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textDisplayName.text = arguments?.getString(EXTRA_NAME)

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            findNavController().navigate(R.id.cusLoginPage)
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}
