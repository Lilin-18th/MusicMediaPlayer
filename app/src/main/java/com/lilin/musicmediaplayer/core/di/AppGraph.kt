package com.lilin.musicmediaplayer.core.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.lilin.musicmediaplayer.core.di.factory.ViewModelFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders, ViewModelGraph {
    fun viewModelFactory(): ViewModelFactory

    @Provides
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    fun provideContentResolver(application: Application): ContentResolver =
        application.contentResolver

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}
