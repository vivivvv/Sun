package com.app.mybase.di

import com.app.mybase.MainActivity
import com.app.mybase.di.modules.MainModule
import com.app.mybase.views.video.VideoActivity
import com.app.mybase.di.modules.VideoModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [VideoModule::class])
    abstract fun provideVideoActivity(): VideoActivity


}