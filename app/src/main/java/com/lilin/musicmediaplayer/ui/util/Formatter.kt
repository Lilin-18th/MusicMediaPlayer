package com.lilin.musicmediaplayer.ui.util

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun Long.formatMillisToSeconds(): String {
    val minutes = milliseconds.inWholeMinutes.toString().padStart(2, '0')
    val seconds = (milliseconds.inWholeSeconds % 60).toString().padStart(2, '0')

    return "$minutes:$seconds"
}

fun Long.formatSeconds(): String {
    val minutes = seconds.inWholeMinutes.toString().padStart(2, '0')
    val seconds = (seconds.inWholeSeconds % 60).toString().padStart(2, '0')

    return "$minutes:$seconds"
}
