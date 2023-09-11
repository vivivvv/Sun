package com.app.mybase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.databinding.IconsLayoutBinding
import com.app.mybase.model.IconModel

class PlaybackIconsAdapter(var iconModelsList: ArrayList<IconModel>, var context: Context) :
    RecyclerView.Adapter<PlaybackIconsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<IconsLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.icons_layout,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.playbackIcon.setImageResource(iconModelsList[position].image);
        holder.iconTitle.text = iconModelsList[position].name
        holder.itemView.setOnClickListener {
            mListener?.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return iconModelsList.size
    }

    class ViewHolder(itemView: IconsLayoutBinding) : RecyclerView.ViewHolder(itemView.root) {
        var iconTitle: TextView = itemView.iconTitle
        var playbackIcon: ImageView = itemView.playbackIcon
    }

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

}