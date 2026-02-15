package com.lilin.musicmediaplayer.data.mapper

import com.lilin.musicmediaplayer.data.entity.MusicEntity
import com.lilin.musicmediaplayer.domain.model.Music

fun MusicEntity.toDomain(): Music {
    return Music(
        id = id,
        title = title,
        artist = artist,
        albumId = albumId,
        duration = duration,
        uri = uri,
    )
}
