package com.lilin.musicmediaplayer.feature.playlist

import com.lilin.musicmediaplayer.domain.model.Music

data class PlayListUiState(
    val musicList: List<Music> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
