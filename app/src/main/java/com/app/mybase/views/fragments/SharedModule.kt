package com.app.mybase.views.fragments

import androidx.lifecycle.ViewModelProvider
import com.app.mybase.helper.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class SharedModule {

    @Provides
    fun provideViewModelProvider(viewModel: SharedViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }

}