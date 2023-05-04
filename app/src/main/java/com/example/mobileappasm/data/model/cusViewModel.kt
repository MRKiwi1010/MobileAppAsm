package com.example.mobileappasm.data.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class cusViewModel : ViewModel() {

    private var customerUsername: String = ""
    private var childname: String = ""
    private var _childKey: String = ""

    fun setChildKey(childKey: String) {
        _childKey = childKey
    }

    fun getChildKey(): String {
        return _childKey
    }

    fun setCustomerUsername(username: String) {
        customerUsername = username
    }

    fun getCustomerUsername(): String {
        return customerUsername
    }

    fun setchildname(childname1: String) {
        childname = childname1
    }

    fun getchildname(): String {
        return childname
    }
}
