package com.app.mybase.views.fragments.frags

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.mybase.BR
import com.app.mybase.MainActivity
import com.app.mybase.R
import com.app.mybase.adapters.MovieListAdapter
import com.app.mybase.adapters.SliderPagerAdapter
import com.app.mybase.base.BaseFragment
import com.app.mybase.databinding.FragmentHomeBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants.POSITION
import com.app.mybase.helper.AppConstants.TRENDING
import com.app.mybase.helper.AppConstants.VIDEO_LIST
import com.app.mybase.helper.AppConstants.WATCH_AGAIN
import com.app.mybase.model.Video
import com.app.mybase.views.fragments.SharedViewModel
import com.app.mybase.views.video.VideoActivity
import com.google.android.material.tabs.TabLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class HomeFragment : BaseFragment<FragmentHomeBinding, SharedViewModel>(),
    MovieListAdapter.MovieListClickListener {

    val TAG = HomeFragment::class.java.toString()
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

    override fun getViewModel(): SharedViewModel =
        ViewModelProvider(this, factory)[SharedViewModel::class.java]

    override fun getBindingVariable(): Int = BR.homeViewModel

    override fun getContentView(): Int = R.layout.fragment_home

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Binding data
        initializeBindingData()
        // Initialize recycler view
        initializeTrendingRV()
        // Initialize recycler view
        initializeWatchAgainRV()
        // Get API data
        updateUIData()
    }

    private fun initializeBindingData() {
        sliderpager = mDataBinding!!.sliderPager
        indicator = mDataBinding!!.indicator
        trendingRecyclerView = mDataBinding!!.videoRecyclerview
        watchAgainRecyclerView = mDataBinding!!.watchAgainRecyclerview
    }

    private fun initializeTrendingRV() {
        trendingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = MovieListAdapter(requireContext(), TRENDING)
        trendingRecyclerView.adapter = trendingAdapter
        trendingAdapter.setOnClickListener(this)
    }

    private fun initializeWatchAgainRV() {
        watchAgainRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        watchAgainAdapter = MovieListAdapter(requireContext(), WATCH_AGAIN)
        watchAgainRecyclerView.adapter = watchAgainAdapter
        watchAgainAdapter.setOnClickListener(this)
    }

    private fun updateUIData() {
        getViewModel().getDataFromLocalJson().observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    updateSliderData(apiResponse.response.sliders)
                    updateTrendingVideoData(apiResponse.response.videos)
                    updateWatchAgainVideoData(apiResponse.response.videos)
                    Log.d(TAG, "updateUIData: $apiResponse")
                }
                is ApisResponse.Error -> {
                    Log.d(TAG, "error message: ${apiResponse.exception}")
                    (requireActivity() as MainActivity).showToast(apiResponse.exception)
                }
                is ApisResponse.Loading -> {
                    getViewModel().showProgress()
                }
                is ApisResponse.Complete -> {
                    getViewModel().hideProgress()
                }
                else -> {}
            }
        })
    }

    private fun updateSliderData(sliders: List<String>) {
        slideDataList.clear()
        slideDataList.addAll(sliders)
        val adapter = SliderPagerAdapter(requireContext(), slideDataList)
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
        Intent(requireContext(), VideoActivity::class.java).apply {
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

    override fun setStatusBarColor() {
        super.setStatusBarColor()
        var window = requireActivity().window
        window.statusBarColor = requireActivity().getColor(R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }


}