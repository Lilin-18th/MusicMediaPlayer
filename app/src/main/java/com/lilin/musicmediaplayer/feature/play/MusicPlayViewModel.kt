package com.lilin.musicmediaplayer.feature.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.domain.repository.MusicPlayerRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class MusicPlayViewModel(
    @Assisted private val music: Music,
    private val musicPlayerRepository: MusicPlayerRepository,
) : ViewModel() {
    val uiState: StateFlow<MusicPlayUiState> =
        musicPlayerRepository.playerState
            .map { playerState ->
                MusicPlayUiState(
                    isPlaying = playerState.isPlaying,
                    currentPosition = playerState.currentPosition,
                    currentMusic = playerState.currentMusic ?: music,
                    duration = playerState.duration,
                    isBuffering = playerState.isBuffering,
                    error = playerState.error,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MusicPlayUiState(currentMusic = music),
            )

    init {
        musicPlayerRepository.setMediaItem(music)
    }

    private fun play() {
        musicPlayerRepository.play()
    }

    private fun pause() {
        musicPlayerRepository.pause()
    }

    fun togglePlayPause() {
        if (uiState.value.isPlaying) {
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

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(music: Music): MusicPlayViewModel
    }
}
