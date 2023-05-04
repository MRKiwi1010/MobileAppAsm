package com.example.mobileappasm

class Donation (
    var bankType: String? = null,
    var amount: String? = null,
    var cardCvv: Int,
    var cardExp: String? = null,
    var cardNo: Int,
    var childName: String? = null,
    var date: String? = null,
    var username: Int
) {
    constructor() : this("", "", 0, "", 0, "", "", 0)
}