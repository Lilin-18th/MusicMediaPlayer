package com.lilin.musicmediaplayer.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val albumId: Long,
    val duration: Long,
    val uri: String,
)
