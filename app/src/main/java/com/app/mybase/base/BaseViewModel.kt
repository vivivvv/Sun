package com.app.mybase.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    var showLoading = MutableLiveData<Boolean>()

    init {
        hideProgress()
    }

    fun showProgress() {
        showLoading.value = true
    }

    fun hideProgress() {
        showLoading.value = false
    }


}