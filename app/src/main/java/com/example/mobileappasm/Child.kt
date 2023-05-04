package com.example.mobileappasm

data class Child(
    var childUrl: String? = null,
    var childName: String = "",
    var childNation: String = "",
    var childAge: Int,
    var child_Des: String = "",
    var target: Double,
    var totalReceived: Double,
) {
    constructor() : this("","","",0, "",0.0,0.0)
}