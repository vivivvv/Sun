package com.app.mybase.views.fragments.frags

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.BR
import com.app.mybase.MainActivity
import com.app.mybase.R
import com.app.mybase.adapters.MovieListAdapter
import com.app.mybase.base.BaseFragment
import com.app.mybase.databinding.FragmentSearchBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants
import com.app.mybase.helper.AppConstants.SEARCH
import com.app.mybase.model.Video
import com.app.mybase.views.fragments.SharedViewModel
import com.app.mybase.views.video.VideoActivity
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

class SearchFragment : BaseFragment<FragmentSearchBinding, SharedViewModel>(),
    MovieListAdapter.MovieListClickListener {

    val TAG = SearchFragment::class.java.toString()
    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: MovieListAdapter
    var searchVideoDataList = ArrayList<Video>()
    var searchedVideoList = ArrayList<Video>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SharedViewModel =
        ViewModelProvider(this, factory)[SharedViewModel::class.java]

    override fun getBindingVariable(): Int = BR.searchViewModel

    override fun getContentView(): Int = R.layout.fragment_search

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
        // Set search listener
        searchListener()
        // Get API data
        updateUIData()
    }

    private fun initializeBindingData() {
        searchView = mDataBinding!!.searchView
        searchRecyclerView = mDataBinding!!.recyclerView
    }

    private fun initializeTrendingRV() {
        searchRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        searchAdapter = MovieListAdapter(requireContext(), SEARCH)
        searchRecyclerView.adapter = searchAdapter
        searchAdapter.setOnClickListener(this)
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
        searchVideoDataList.clear()
        searchVideoDataList.addAll(videos)
        searchAdapter.setMovieList(searchVideoDataList)
        // Update full video list
        searchedVideoList = searchVideoDataList
        // Update movie to UI
        getViewModel().hideNoDataText()
    }

    private fun searchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Video>()
            for (i in searchVideoDataList) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                getViewModel().showNoDataText()
            } else {
                // Update filtered video list
                searchedVideoList = filteredList
                searchAdapter.setMovieList(filteredList)
                getViewModel().hideNoDataText()
            }
        }
    }

    override fun onStop() {
        // Clear search view value
        searchView.setQuery("", true)
        super.onStop()
    }

    override fun onClicked(listPosition: Int, from: String) {
        // Close keyboard
        (requireActivity() as MainActivity).hideKeyboard()
        searchView.clearFocus()
        // Send data to Video Activity
        Intent(requireContext(), VideoActivity::class.java).apply {
            putExtra(AppConstants.VIDEO_LIST, searchedVideoList)
            putExtra(AppConstants.POSITION, listPosition)
            startActivity(this)
        }
    }

}