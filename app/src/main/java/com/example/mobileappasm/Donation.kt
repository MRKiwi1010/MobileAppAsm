package com.example.mobileappasm

class Donation (
    var bankType: String? = null,
    var cardNo: String? = null,
    var cardCvv: String? = null,
    var cardExp: String? = null,
    var amount: Double,
    var date: String? = null,
    var time: String? = null,
    var childName: String? = null,
    var username: String? = null
) {
    constructor() : this("", "","", "", 0.0, "", "", "", "")
}