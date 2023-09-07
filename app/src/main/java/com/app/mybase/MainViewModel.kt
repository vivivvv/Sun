package com.app.mybase


import android.app.Application
import androidx.lifecycle.liveData
import com.app.mybase.base.BaseViewModel
import com.app.mybase.helper.ApisResponse
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    var application: Application
) : BaseViewModel() {

    fun getDataFromLocalJson() = liveData(Dispatchers.IO) {
        emit(ApisResponse.Loading)
        emit(mainRepository.getDataFromLocalJson(application))
        emit(ApisResponse.Complete)
    }

}