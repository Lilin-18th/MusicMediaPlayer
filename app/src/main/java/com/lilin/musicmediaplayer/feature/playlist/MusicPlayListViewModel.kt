package com.lilin.musicmediaplayer.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lilin.musicmediaplayer.domain.usecase.GetMusicListUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(MusicPlayListViewModel::class)
@Inject
class MusicPlayListViewModel(
    private val useCase: GetMusicListUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayListUiState())
    val uiState: StateFlow<PlayListUiState> = _uiState.asStateFlow()

    fun loadMusicList() {
        viewModelScope.launch {
            useCase()
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch {
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            errorMessage = it.message,
                        )
                    }
                }
                .collect { musicList ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            musicList = musicList,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }
}
