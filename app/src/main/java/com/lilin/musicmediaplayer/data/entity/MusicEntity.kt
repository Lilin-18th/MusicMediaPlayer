package com.lilin.musicmediaplayer.data.entity

data class MusicEntity(
    val id: Long,
    val title: String,
    val artist: String,
    val albumId: Long,
    val duration: Long,
    val uri: String,
)
