package com.app.mybase.di

import android.app.Application
import com.app.mybase.MyApp
import com.app.mybase.di.modules.ApiModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class, ApiModule::class, ActivityBuilder::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

    /*  This is our custom Application class*/
    fun inject(application: MyApp)
}