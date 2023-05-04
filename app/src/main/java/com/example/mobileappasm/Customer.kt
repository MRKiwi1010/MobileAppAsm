package com.example.mobileappasm

data class Customer(
    var email: String = "",
    var name: String = "",
    var password: String = "",
    var username: String = "",
    var userimg: String = ""
) {
    constructor() : this("", "", "", "", "")
}
