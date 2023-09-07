package com.app.mybase.di.modules

import androidx.lifecycle.ViewModelProvider
import com.app.mybase.helper.ViewModelProviderFactory
import com.app.mybase.views.video.VideoViewModel
import dagger.Module
import dagger.Provides

@Module
class VideoModule {

    @Provides
    fun provideViewModelProvider(viewModel: VideoViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}