package com.example.mobileappasm

data class Child(
    var childId: Int,
    val childName: String = "",
    val childNation: String = "",
    val childAge: Int,
    val totalReceived: Double,
    val durationLeft: Int
) {
    constructor() : this(0, "", "", 0, 0.0, 0)
}