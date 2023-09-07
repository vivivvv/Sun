package com.app.mybase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseData(
    val name: String,
    val sliders: List<String>,
    val videos: List<Video>
) : Parcelable

@Parcelize
data class Video(
    val description: String,
    val sources: List<String>,
    val subtitle: String,
    val thumb: String,
    val title: String
) : Parcelable