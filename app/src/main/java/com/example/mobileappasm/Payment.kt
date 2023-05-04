package com.example.mobileappasm

import java.util.Date

data class Payment (
    val bankType: String = "",
    val amount: Double = 0.0,
    val cardCVV: String = "",
    val cardExp: String = "",
    val cardNo: String = "",
    val childName: String = "",
    val date: String = "",
    val time: String ="",
    val username: String = ""

)