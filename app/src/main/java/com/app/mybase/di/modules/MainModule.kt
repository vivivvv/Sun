package com.app.mybase.di.modules

import androidx.lifecycle.ViewModelProvider
import com.app.mybase.MainViewModel
import com.app.mybase.helper.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun provideViewModelProvider(viewModel: MainViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}