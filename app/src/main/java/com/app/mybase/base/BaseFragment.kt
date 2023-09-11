package com.app.mybase.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.mybase.R

abstract class BaseFragment<V : ViewDataBinding, out T : BaseViewModel> : Fragment() {
    protected var mDataBinding: V? = null
    private var mViewModel: T? = null
    abstract fun getViewModel(): T?

    abstract fun getBindingVariable(): Int

    abstract fun getContentView(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColor()
        mDataBinding = DataBindingUtil.inflate(inflater, getContentView(), container, false)
        performDataBinding()
        return mDataBinding?.root
    }

    private fun performDataBinding() {
        getViewModel()?.let { viewModel ->
            mViewModel = ViewModelProvider(this)[viewModel::class.java]
            mDataBinding?.setVariable(getBindingVariable(), mViewModel)
            mDataBinding?.lifecycleOwner = this
            mDataBinding?.executePendingBindings()
        }
    }

    open fun setStatusBarColor() {
        var window = requireActivity().window
        window.statusBarColor = requireActivity().getColor(R.color.dark)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }
}