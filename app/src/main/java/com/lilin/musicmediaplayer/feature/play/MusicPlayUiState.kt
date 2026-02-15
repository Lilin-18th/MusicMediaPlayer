package com.lilin.musicmediaplayer.feature.play

import com.lilin.musicmediaplayer.domain.model.Music

data class MusicPlayUiState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val currentMusic: Music,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
    val error: String? = null,
) {
    val progress: Float
        get() = if (duration > 0) {
            (currentPosition.toFloat() / duration).coerceIn(0f, 1f)
        } else {
            0f
        }

    val currentSeconds: Long get() = currentPosition / 1000

    val totalSeconds: Long get() = duration / 1000
}
