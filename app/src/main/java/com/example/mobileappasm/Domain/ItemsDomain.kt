package com.example.mobileappasm.Domain

import java.io.Serializable

class ItemsDomain(
    val child_name: String,
    val childNation: String,
    val child_Des: String,
    val totalReceive: Double,
    val target:Int,
    val child_pic: String,  // new property to store the URL of the child's image
    val child_age: Int
) : Serializable
