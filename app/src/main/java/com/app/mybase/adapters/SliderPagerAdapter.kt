package com.app.mybase.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.app.mybase.R
import com.app.mybase.helper.Utils


class SliderPagerAdapter(var mContext: Context, var mList: List<String>) : PagerAdapter() {

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val slideLayout: View = inflater.inflate(R.layout.slide_item, null)

        val slideImg: ImageView = slideLayout.findViewById(R.id.slide_img)
        Utils.setMovieImage(mContext, mList[position], slideImg)
        container.addView(slideLayout)
        return slideLayout
    }


    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as View)
    }

}