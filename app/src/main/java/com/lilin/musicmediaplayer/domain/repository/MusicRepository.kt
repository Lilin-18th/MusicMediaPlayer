package com.lilin.musicmediaplayer.domain.repository

import com.lilin.musicmediaplayer.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getMusicList(): Flow<List<Music>>
}
