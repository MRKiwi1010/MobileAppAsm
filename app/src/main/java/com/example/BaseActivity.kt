package com.example

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.paypal.android.sdk.payments.LoginActivity


open class BaseActivity : Fragment() {

    override fun onResume() {
        super.onResume()

        // Check if user is logged in
        if (!isLoggedIn) {
            // Redirect to login page
            val intent = Intent(getActivity(), LoginActivity::class.java)
            startActivity(intent)
            getActivity()?.finish()
        }
    }

    // Check if user is logged in, e.g. by checking a shared preference value or server-side session
    // Return true if logged in, false otherwise
    private val isLoggedIn: Boolean
        private get() {
            // Check if user is logged in, e.g. by checking a shared preference value or server-side session
            // Return true if logged in, false otherwise
            return true
        }
}
