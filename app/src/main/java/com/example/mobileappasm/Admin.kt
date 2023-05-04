package com.example.mobileappasm

data class Admin(
    var imageUri: String? = null,
    var name: String = "",
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var contact: String = "",
    var gender: String = "",
    var age: String = "",
    val position: String = ""
) {
    constructor() : this("", "", "", "", "", "", "", "", "")
}