package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminProfile : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val username = arguments?.getString("username")
        val password = arguments?.getString("password")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (loginFragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(loginFragment).commit()
        }

        val welcomeMessage = view.findViewById<TextView>(R.id.welcome)
        welcomeMessage.text = "Welcome, ${arguments?.getString(ARG_USERNAME)}!"
    }

    companion object {
        const val ARG_USERNAME = "username"
        const val ARG_PASSWORD = "password"


        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
        fun newInstance(username: String, password: String): AdminProfile {
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putString(ARG_PASSWORD, password)
            }
            val fragment = AdminProfile().apply {
                arguments = args
            }
            return fragment
        }


    }
}