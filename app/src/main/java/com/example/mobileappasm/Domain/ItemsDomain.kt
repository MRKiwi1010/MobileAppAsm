package com.example.mobileappasm.Domain

import java.io.Serializable

class ItemsDomain(
    val title: String,
    val address: String,
    val description: String,
    val bed: Int,
    val bath: Int,
    val price: Int,
    val pic: String,
    val isWifi: Boolean
) : Serializable
