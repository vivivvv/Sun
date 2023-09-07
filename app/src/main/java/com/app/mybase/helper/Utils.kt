package com.app.mybase.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import com.app.mybase.R
import com.app.mybase.model.ResponseData
import com.app.mybase.model.Video
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

// Class help to connect common functions
object Utils {

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? =
        when {
            SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setMovieImage(
        context: Context,
        movieImage: String?,
        imageView: ImageView
    ) {
        if (movieImage != null) {
            Glide.with(context)
                .load(movieImage)
                .error(R.color.white)
                .into(imageView)
        } else {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.with(context).clear(imageView)
            imageView.setImageDrawable(context.getDrawable(R.color.white))
        }
    }

    fun getDataFromLocalJson(context: Context, fileName: String): ResponseData {
        val videoList = ArrayList<Video>()
        val obj = JSONObject(loadJSONFromAsset(context, fileName))
        val name = obj.getString("name")
        val slidersArray = obj.getJSONArray("sliders")
        val jsonArray = obj.getJSONArray("videos")
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray[i] as JSONObject
            val description = jsonObject.getString("description")
            val subtitle = jsonObject.getString("subtitle")
            val thumb = jsonObject.getString("thumb")
            val title = jsonObject.getString("title")
            val sourcesArray = jsonObject.getJSONArray("sources")
            videoList.add(Video(description, jsonArrayToList(sourcesArray), subtitle, thumb, title))
        }
        return ResponseData(name, jsonArrayToList(slidersArray), videoList)
    }

    fun jsonArrayToList(jsonArray: JSONArray): List<String> {
        val list = ArrayList<String>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray[i]
            list.add(item as String)
        }
        return list
    }

    // Method for read JSON file
    fun loadJSONFromAsset(context: Context, fileName: String): String {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Log.d("TAG", "onCreate: ioException $ioException")
        }
        return jsonString
    }

}
