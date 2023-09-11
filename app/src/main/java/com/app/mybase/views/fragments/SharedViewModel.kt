package com.app.mybase.views.fragments

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.mybase.base.BaseViewModel
import com.app.mybase.helper.ApisResponse
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    private val sharedRepository: SharedRepository,
    var application: Application
) : BaseViewModel() {

    var noDataText = MutableLiveData<Boolean>()
    var showData = MutableLiveData<Boolean>()

    init {
        hideNoDataText()
    }

    fun showNoDataText() {
        noDataText.value = true
        showData.value = false
    }

    fun hideNoDataText() {
        noDataText.value = false
        showData.value = true
    }

    fun getDataFromLocalJson() = liveData(Dispatchers.IO) {
        emit(ApisResponse.Loading)
        emit(sharedRepository.getDataFromLocalJson(application))
        emit(ApisResponse.Complete)
    }

}