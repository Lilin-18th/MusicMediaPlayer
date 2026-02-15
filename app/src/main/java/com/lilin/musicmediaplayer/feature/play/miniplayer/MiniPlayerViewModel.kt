package com.lilin.musicmediaplayer.feature.play.miniplayer

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
@ViewModelKey(MiniPlayerViewModel::class)
@Inject
class MiniPlayerViewModel(
    private val musicPlayerRepository: MusicPlayerRepository,
) : ViewModel() {
    val miniPlayerUiState: StateFlow<MiniPlayerUiState> = musicPlayerRepository.playerState
        .map { playerState ->
            MiniPlayerUiState(
                currentMusic = playerState.currentMusic,
                isPlaying = playerState.isPlaying,
                progress = if (playerState.duration > 0) {
                    (playerState.currentPosition.toFloat() / playerState.duration).coerceIn(0f, 1f)
                } else {
                    0f
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MiniPlayerUiState(),
        )

    fun toggleMiniPlayerPause() {
        val state = musicPlayerRepository.playerState.value
        if (state.isPlaying) {
            musicPlayerRepository.pause()
        } else {
            musicPlayerRepository.play()
        }
    }

    fun miniPlayerSkipToNext() {
        musicPlayerRepository.skipToNext()
    }
}
