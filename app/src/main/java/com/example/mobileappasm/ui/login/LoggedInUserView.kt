package com.example.mobileappasm.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val username: String = "",
    val password: String = ""
)