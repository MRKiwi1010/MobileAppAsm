package com.example.mobileappasm.ui.login

import androidx.lifecycle.ViewModel

class AdminViewModel : ViewModel() {
    var username: String = ""
        get() = field
        set(value) {
            field = value
        }
}