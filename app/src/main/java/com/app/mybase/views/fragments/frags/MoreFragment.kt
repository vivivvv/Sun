package com.app.mybase.views.fragments.frags

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.mybase.BR
import com.app.mybase.R
import com.app.mybase.base.BaseFragment
import com.app.mybase.databinding.FragmentMoreBinding
import com.app.mybase.views.fragments.SharedViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MoreFragment : BaseFragment<FragmentMoreBinding, SharedViewModel>() {

    val TAG = MoreFragment::class.java.toString()

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SharedViewModel =
        ViewModelProvider(this, factory)[SharedViewModel::class.java]

    override fun getBindingVariable(): Int = BR.moreViewModel

    override fun getContentView(): Int = R.layout.fragment_more

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}