package com.app.mybase.di

import com.app.mybase.MainActivity
import com.app.mybase.di.modules.MainModule
import com.app.mybase.di.modules.VideoModule
import com.app.mybase.views.fragments.SharedModule
import com.app.mybase.views.fragments.frags.*
import com.app.mybase.views.video.VideoActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [VideoModule::class])
    abstract fun provideVideoActivity(): VideoActivity

    @ContributesAndroidInjector(modules = [SharedModule::class])
    abstract fun provideHomeFragment(): HomeFragment

    @ContributesAndroidInjector(modules = [SharedModule::class])
    abstract fun provideSearchFragment(): SearchFragment

    @ContributesAndroidInjector(modules = [SharedModule::class])
    abstract fun provideComingSoonFragment(): ComingSoonFragment

    @ContributesAndroidInjector(modules = [SharedModule::class])
    abstract fun provideDownloadFragment(): DownloadFragment

    @ContributesAndroidInjector(modules = [SharedModule::class])
    abstract fun provideMoreFragment(): MoreFragment

}