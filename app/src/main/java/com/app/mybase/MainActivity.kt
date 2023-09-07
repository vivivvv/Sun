package com.app.mybase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.mybase.adapters.MovieListAdapter
import com.app.mybase.adapters.SliderPagerAdapter
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityMainBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants.POSITION
import com.app.mybase.helper.AppConstants.TRENDING
import com.app.mybase.helper.AppConstants.VIDEO_LIST
import com.app.mybase.helper.AppConstants.WATCH_AGAIN
import com.app.mybase.model.Video
import com.app.mybase.views.video.VideoActivity
import com.google.android.material.tabs.TabLayout
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(), MovieListAdapter.MovieListClickListener {

    val TAG = this::class.java.name
    lateinit var binding: ActivityMainBinding
    lateinit var viewmodel: MainViewModel

    private lateinit var trendingRecyclerView: RecyclerView
    private lateinit var trendingAdapter: MovieListAdapter
    var trendingVideoDataList = ArrayList<Video>()

    lateinit var watchAgainRecyclerView: RecyclerView
    lateinit var watchAgainAdapter: MovieListAdapter
    var watchAgainVideoDataList = ArrayList<Video>()

    var slideDataList = ArrayList<String>()
    lateinit var sliderpager: ViewPager
    lateinit var indicator: TabLayout

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewmodel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding.mainViewModel = viewmodel
        binding.lifecycleOwner = this@MainActivity

        initializeBindingData()
        // Initialize recycler view
        initializeTrendingRV()
        // Initialize recycler view
        initializeWatchAgainRV()
        // Get API data
        updateUIData()

    }

    private fun initializeBindingData() {
        sliderpager = binding.sliderPager
        indicator = binding.indicator
        trendingRecyclerView = binding.videoRecyclerview
        watchAgainRecyclerView = binding.watchAgainRecyclerview
    }

    private fun initializeTrendingRV() {
        trendingRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = MovieListAdapter(this, TRENDING)
        trendingRecyclerView.adapter = trendingAdapter
        trendingAdapter.setOnClickListener(this)
    }

    private fun initializeWatchAgainRV() {
        watchAgainRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        watchAgainAdapter = MovieListAdapter(this, WATCH_AGAIN)
        watchAgainRecyclerView.adapter = watchAgainAdapter
        watchAgainAdapter.setOnClickListener(this)
    }

    private fun updateUIData() {
        viewmodel.getDataFromLocalJson().observe(this, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    updateSliderData(apiResponse.response.sliders)
                    updateTrendingVideoData(apiResponse.response.videos)
                    updateWatchAgainVideoData(apiResponse.response.videos)
                    Log.d(TAG, "updateUIData: $apiResponse")
                }
                is ApisResponse.Error -> {
                    Log.d(TAG, "error message: ${apiResponse.exception}")
                    showToast(apiResponse.exception)
                }
                is ApisResponse.Loading -> {
                    viewmodel.showProgress()
                }
                is ApisResponse.Complete -> {
                    viewmodel.hideProgress()
                }
                else -> {}
            }
        })
    }

    private fun updateSliderData(sliders: List<String>) {
        slideDataList.clear()
        slideDataList.addAll(sliders)
        val adapter = SliderPagerAdapter(this, slideDataList)
        sliderpager.adapter = adapter
        // setup timer for auto slide
        autoSlideOption()
        indicator.setupWithViewPager(sliderpager, true)
    }

    private fun updateTrendingVideoData(videos: List<Video>) {
        trendingVideoDataList.clear()
        trendingVideoDataList.addAll(videos)
        trendingAdapter.setMovieList(trendingVideoDataList)
    }

    private fun updateWatchAgainVideoData(videos: List<Video>) {
        watchAgainVideoDataList.clear()
        watchAgainVideoDataList.addAll(videos)
        watchAgainVideoDataList.shuffle()
        watchAgainAdapter.setMovieList(watchAgainVideoDataList)
    }

    override fun onClicked(listPosition: Int, from: String) {
        val list = when (from) {
            TRENDING -> {
                trendingVideoDataList
            }
            WATCH_AGAIN -> {
                watchAgainVideoDataList
            }
            else -> {
                ArrayList()
            }
        }
        // Send data to Video Activity
        Intent(this, VideoActivity::class.java).apply {
            putExtra(VIDEO_LIST, list)
            putExtra(POSITION, listPosition)
            startActivity(this)
        }
    }

    private fun autoSlideOption() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (sliderpager.currentItem < slideDataList.size - 1) {
                        sliderpager.currentItem = sliderpager.currentItem + 1
                    } else {
                        sliderpager.currentItem = 0
                    }
                }
            }
        }, 6000, 8000)
    }


}