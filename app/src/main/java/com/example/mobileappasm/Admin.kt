package com.example.mobileappasm

data class Admin(
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val contact: String = "",
    val gender: String = "",
    val age: String = ""
) {
    constructor() : this("", "", "", "", "", "", "")
}
