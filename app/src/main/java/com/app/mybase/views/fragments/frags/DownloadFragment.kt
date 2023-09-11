package com.app.mybase.views.fragments.frags

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.BR
import com.app.mybase.MainActivity
import com.app.mybase.R
import com.app.mybase.adapters.MovieListAdapter
import com.app.mybase.base.BaseFragment
import com.app.mybase.databinding.FragmentDownloadBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants
import com.app.mybase.helper.AppConstants.DOWNLOAD
import com.app.mybase.model.Video
import com.app.mybase.views.fragments.SharedViewModel
import com.app.mybase.views.video.VideoActivity
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DownloadFragment : BaseFragment<FragmentDownloadBinding, SharedViewModel>(),
    MovieListAdapter.MovieListClickListener {

    val TAG = DownloadFragment::class.java.toString()
    private lateinit var downloadRecyclerView: RecyclerView
    private lateinit var downloadAdapter: MovieListAdapter
    var downloadVideoDataList = ArrayList<Video>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SharedViewModel =
        ViewModelProvider(this, factory)[SharedViewModel::class.java]

    override fun getBindingVariable(): Int = BR.downloadViewModel

    override fun getContentView(): Int = R.layout.fragment_download

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize recycler view
        initializeTrendingRV()
        // Get API data
        updateUIData()
    }

    private fun initializeTrendingRV() {
        downloadRecyclerView = mDataBinding!!.recyclerView
        downloadRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        downloadAdapter = MovieListAdapter(requireContext(), DOWNLOAD)
        downloadRecyclerView.adapter = downloadAdapter
        downloadAdapter.setOnClickListener(this)
    }

    private fun updateUIData() {
        getViewModel().getDataFromLocalJson().observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    updateSearchVideoData(apiResponse.response.videos)
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

    private fun updateSearchVideoData(videos: List<Video>) {
        downloadVideoDataList.clear()
        downloadVideoDataList.addAll(videos)
        downloadAdapter.setMovieList(downloadVideoDataList)
        if (downloadVideoDataList.isEmpty()) {
            getViewModel().showNoDataText()
        } else {
            getViewModel().hideNoDataText()
        }
    }

    override fun onClicked(listPosition: Int, from: String) {
        // Send data to Video Activity
        Intent(requireContext(), VideoActivity::class.java).apply {
            putExtra(AppConstants.VIDEO_LIST, downloadVideoDataList)
            putExtra(AppConstants.POSITION, listPosition)
            startActivity(this)
        }
    }

}