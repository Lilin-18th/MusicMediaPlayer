package com.lilin.musicmediaplayer.domain.model

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentMusic: Music? = null,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val isBuffering: Boolean = false,
    val error: String? = null,
)
