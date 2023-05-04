package com.example.mobileappasm

data class Child(
    var childUrl: String? = null,
    val childName: String = "",
    val childNation: String = "",
    val childAge: Int,
    val child_Des: String = "",
    val target: Double,
    val totalReceived: Double,
) {
    constructor() : this("","","",0, "",0.0,0.0)
}