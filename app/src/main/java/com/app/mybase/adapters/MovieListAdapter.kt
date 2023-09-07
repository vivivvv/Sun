package com.app.mybase.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.databinding.MovieCardViewBinding
import com.app.mybase.helper.Utils
import com.app.mybase.model.Video

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
        val view = DataBindingUtil.inflate<MovieCardViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.movie_card_view,
            parent,
            false
        )
        return MovieListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        // Set text on radio button
        holder.setData(position)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieListViewHolder(itemView: MovieCardViewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        // Assign variable
        var movieImage: ImageView = itemView.movieImageview

        fun setData(position: Int) {
            // Update Image
            Utils.setMovieImage(context, movieList[position].thumb, movieImage)
            itemView.setOnClickListener {
                movieListClickListener?.onClicked(position, from)
            }
        }
    }

    private var movieListClickListener: MovieListClickListener? = null

    // Initializing TimeSlotIconClickListener Interface
    fun setOnClickListener(listener: MovieListClickListener) {
        movieListClickListener = listener
    }

    // Interface For TimeSlot Icon Click
    interface MovieListClickListener {
        fun onClicked(listPosition: Int, from: String)
    }

}