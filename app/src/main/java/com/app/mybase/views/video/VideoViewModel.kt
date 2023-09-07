package com.app.mybase.views.video

import android.app.Application
import com.app.mybase.base.BaseViewModel
import com.app.mybase.model.Video
import javax.inject.Inject

class VideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    var application: Application
) : BaseViewModel() {

    var position: Int = 0
    var videoDataList = ArrayList<Video>()

}