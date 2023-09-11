package com.app.mybase.views.fragments

import android.app.Application
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.Utils
import com.app.mybase.model.ResponseData
import com.app.mybase.network.ApiStories
import javax.inject.Inject

class SharedRepository @Inject constructor(var apiStories: ApiStories) {

    fun getDataFromLocalJson(application: Application): ApisResponse<ResponseData> {
        return try {
            val list =
                Utils.getDataFromLocalJson(application.applicationContext, "sample_file.json")
            ApisResponse.Success(list)
        } catch (e: Exception) {
            ApisResponse.Error(e.message.toString())
        }
    }

}