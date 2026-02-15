package com.lilin.musicmediaplayer.feature.play.miniplayer

import com.lilin.musicmediaplayer.domain.model.Music

data class MiniPlayerUiState(
    val currentMusic: Music? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
)
