package com.example.mobileappasm

data class Customer(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val username: String = ""
) {
    constructor() : this("", "", "", "")
}
