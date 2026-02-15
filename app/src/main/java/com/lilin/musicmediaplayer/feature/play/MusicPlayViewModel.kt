package com.lilin.musicmediaplayer.feature.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lilin.musicmediaplayer.domain.repository.MusicPlayerRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@ContributesIntoMap(AppScope::class)
@ViewModelKey(MusicPlayViewModel::class)
@Inject
class MusicPlayViewModel(
    private val musicPlayerRepository: MusicPlayerRepository,
) : ViewModel() {
    val uiState: StateFlow<MusicPlayUiState?> =
        musicPlayerRepository.playerState
            .map { playerState ->
                playerState.currentMusic?.let { music ->
                    MusicPlayUiState(
                        isPlaying = playerState.isPlaying,
                        currentPosition = playerState.currentPosition,
                        currentMusic = music,
                        duration = playerState.duration,
                        isBuffering = playerState.isBuffering,
                        error = playerState.error,
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

//    init {
//        musicPlayerRepository.setMediaItem(music)
//    }

    private fun play() {
        musicPlayerRepository.play()
    }

    private fun pause() {
        musicPlayerRepository.pause()
    }

    fun togglePlayPause() {
        val state = musicPlayerRepository.playerState.value
        if (state.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(positionMs: Long) {
        musicPlayerRepository.seekTo(positionMs)
    }

    fun skipForward(seconds: Long = 5) {
        musicPlayerRepository.skipForward(seconds)
    }

    fun skipBackward(seconds: Long = 5) {
        musicPlayerRepository.skipBackward(seconds)
    }

    fun skipToPrevious() {
        musicPlayerRepository.skipToPrevious()
    }

    fun skipToNext() {
        musicPlayerRepository.skipToNext()
    }
}
