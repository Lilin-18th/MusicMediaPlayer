package com.lilin.musicmediaplayer.domain.repository

import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface MusicPlayerRepository {
    val playerState: StateFlow<PlayerState>

    fun setPlayList(musicList: List<Music>, startIndex: Int = 0)
    fun setMediaItem(music: Music)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(positionMs: Long)
    fun skipForward(seconds: Long)
    fun skipBackward(seconds: Long)
    fun skipToNext()
    fun skipToPrevious()
    fun playAtIndex(index: Int)
}
