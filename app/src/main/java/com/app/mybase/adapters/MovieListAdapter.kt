package com.app.mybase.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.databinding.DownloadCardViewBinding
import com.app.mybase.databinding.MovieCardViewBinding
import com.app.mybase.databinding.SearchCardViewBinding
import com.app.mybase.helper.AppConstants.DOWNLOAD
import com.app.mybase.helper.AppConstants.TRENDING
import com.app.mybase.helper.AppConstants.WATCH_AGAIN
import com.app.mybase.helper.Utils
import com.app.mybase.model.Video

// Designed Adapter suitable for all bottom nav menus
class MovieListAdapter(val context: Context, val from: String) :
    RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

    private var movieList = ArrayList<Video>()

    @SuppressLint("NotifyDataSetChanged")
    fun setMovieList(
        movieList: ArrayList<Video>
    ) {
        this.movieList = movieList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        // Initialize view
        val view = when (from) {
            TRENDING, WATCH_AGAIN -> {
                DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.movie_card_view,
                    parent,
                    false
                )
            }
            DOWNLOAD -> {
                DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.download_card_view,
                    parent,
                    false
                )
            }
            else -> {
                DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.search_card_view,
                    parent,
                    false
                )
            }
        }

        return MovieListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        // Set text on radio button
        holder.setData(position)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieListViewHolder(itemView: ViewDataBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var movieImage: ImageView? = null
        var title: TextView? = null
        var description: TextView? = null

        init {
            when (from) {
                TRENDING, WATCH_AGAIN -> {
                    movieImage = (itemView as MovieCardViewBinding).movieImageview
                }
                DOWNLOAD -> {
                    movieImage = (itemView as DownloadCardViewBinding).movieImageview
                    title = (itemView as DownloadCardViewBinding).titleText
                    description = (itemView as DownloadCardViewBinding).descriptionText
                }
                else -> {
                    movieImage = (itemView as SearchCardViewBinding).movieImageview
                    title = (itemView as SearchCardViewBinding).titleText
                }
            }
        }

        fun setData(position: Int) {
            when (from) {
                TRENDING, WATCH_AGAIN -> {
                    // Update Image
                    Utils.setMovieImage(context, movieList[position].thumb, movieImage!!)
                }
                DOWNLOAD -> {
                    // Update Image
                    Utils.setMovieImage(context, movieList[position].thumb, movieImage!!)
                    // Update Title
                    title?.text = movieList[position].title
                    // Update Title
                    description?.text = movieList[position].description
                }
                else -> {
                    // Update Image
                    Utils.setMovieImage(context, movieList[position].thumb, movieImage!!)
                    // Update Title
                    title?.text = movieList[position].title
                }
            }
            itemView.setOnClickListener {
                movieListClickListener?.onClicked(position, from)
            }
        }
    }

    private var movieListClickListener: MovieListClickListener? = null

    // Initializing Interface
    fun setOnClickListener(listener: MovieListClickListener) {
        movieListClickListener = listener
    }

    // Interface For Image Click
    interface MovieListClickListener {
        fun onClicked(listPosition: Int, from: String)
    }

}