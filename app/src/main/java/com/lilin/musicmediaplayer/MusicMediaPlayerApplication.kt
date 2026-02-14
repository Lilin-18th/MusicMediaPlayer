package com.lilin.musicmediaplayer

import android.app.Application
import com.lilin.musicmediaplayer.core.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class MusicMediaPlayerApplication : Application(), MetroApplication {
    private val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph
}
