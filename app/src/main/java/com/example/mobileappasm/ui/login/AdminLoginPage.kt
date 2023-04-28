package com.example.mobileappasm.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.*
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobileappasm.databinding.FragmentAdminLoginPageBinding
import com.example.mobileappasm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

const val USERNAME_KEY = "username"

class AdminLoginPage : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var adminViewModel: AdminViewModel
    private var _binding: FragmentAdminLoginPageBinding? = null
    private lateinit var auth: FirebaseAuth

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        val username = binding.username
        val password = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        binding.login.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()

            auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // sign-in success
                    val user = auth.currentUser
                    if (user != null) {
                        updateUiWithUser(LoggedInUserView(username))
                    }
                } else {
                    // sign-in fail
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        Toast.makeText(
                            requireContext(),
                            "Login failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Login failed: ${exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    username.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    password.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)

//                    val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
//                        getString(R.string.preference_file_key), Context.MODE_PRIVATE
//                    )
//                    with(sharedPref?.edit()) {
//                        this?.putString(USERNAME_KEY, it.displayName)
//                        this?.apply()
//                    }
//                    val bundle = bundleOf("username" to it.displayName)
//                    findNavController().navigate(R.id.action_adminLoginPage_to_adminProfile, bundle)
                    findNavController().navigate(R.id.action_adminLoginPage_to_adminProfile)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }
        }
        username.addTextChangedListener(afterTextChangedListener)
        password.addTextChangedListener(afterTextChangedListener)
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    username.text.toString(),
                    password.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                username.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.username
        val appContext = context?.applicationContext ?: return

        // create session by storing the username in SharedPreferences
//        val pref = getSharedPreferences()
//        val editor = pref.edit()
//        editor.putString(USERNAME_KEY, model.username)
//        editor.apply()

        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()

        //Navigation to Admin Profile
        navigateToAdminProfile()
    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome)
//        val displayName = model.displayName
//        Toast.makeText(
//            context,
//            "$welcome $displayName",
//            Toast.LENGTH_LONG
//        ).show()
//    }


    private fun navigateToAdminProfile() {
        val username = getSharedPreferences().getString(USERNAME_KEY, "") ?: ""
        val bundle = bundleOf("username" to username)
        findNavController().navigate(R.id.action_adminLoginPage_to_adminProfile, bundle)
    }


    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val USERNAME_KEY = "username"
    }
}