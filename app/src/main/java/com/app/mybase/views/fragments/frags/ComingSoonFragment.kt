package com.app.mybase.views.fragments.frags

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.BR
import com.app.mybase.MainActivity
import com.app.mybase.R
import com.app.mybase.adapters.MovieListAdapter
import com.app.mybase.base.BaseFragment
import com.app.mybase.databinding.FragmentComingSoonBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants
import com.app.mybase.model.Video
import com.app.mybase.views.fragments.SharedViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ComingSoonFragment : BaseFragment<FragmentComingSoonBinding, SharedViewModel>() {

    val TAG = ComingSoonFragment::class.java.toString()
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: MovieListAdapter
    var searchVideoDataList = ArrayList<Video>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SharedViewModel =
        ViewModelProvider(this, factory)[SharedViewModel::class.java]

    override fun getBindingVariable(): Int = BR.comingSoonViewModel

    override fun getContentView(): Int = R.layout.fragment_coming_soon

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
        searchRecyclerView = mDataBinding!!.recyclerView
        searchRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        searchAdapter = MovieListAdapter(requireContext(), AppConstants.SEARCH)
        searchRecyclerView.adapter = searchAdapter
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
        if (searchVideoDataList.isEmpty()) {
            getViewModel().showNoDataText()
        } else {
            getViewModel().hideNoDataText()
        }
    }

}