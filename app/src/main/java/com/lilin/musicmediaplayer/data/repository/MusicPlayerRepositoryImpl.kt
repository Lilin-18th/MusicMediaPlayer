package com.lilin.musicmediaplayer.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.lilin.musicmediaplayer.core.service.MusicMediaSessionService
import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.domain.model.PlayerState
import com.lilin.musicmediaplayer.domain.repository.MusicPlayerRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class MusicPlayerRepositoryImpl(
    context: Context,
) : MusicPlayerRepository {
    private val sessionToken = SessionToken(
        context,
        ComponentName(context, MusicMediaSessionService::class.java),
    )

    private val controllerFeature: ListenableFuture<MediaController> = MediaController
        .Builder(context, sessionToken)
        .buildAsync()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _playlistState = MutableStateFlow(PlaylistState())

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState

    @Volatile
    private var controller: MediaController? = null

    init {
        controllerFeature.addListener(
            {
                controller = controllerFeature.get()
                setupListener()
                startPositionUpdate()
            },
            MoreExecutors.directExecutor(),
        )
    }

    private fun setupListener() {
        controller?.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    val isBuffering = playbackState == Player.STATE_BUFFERING
                    _playerState.update { playerState ->
                        val duration = if (playbackState == Player.STATE_READY) {
                            controller?.duration ?: playerState.duration
                        } else {
                            playerState.duration
                        }

                        playerState.copy(
                            isBuffering = isBuffering,
                            duration = duration,
                        )
                    }

                    if (playbackState == Player.STATE_ENDED) {
                        skipToNext()
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    _playerState.value = _playerState.value.copy(
                        error = error.message ?: "Unknown error",
                        isPlaying = false,
                    )
                }
            },
        )
    }

    private fun startPositionUpdate() {
        scope.launch {
            while (true) {
                delay(100)
                val controller = this@MusicPlayerRepositoryImpl.controller
                if (controller?.isPlaying == true) {
                    _playerState.update { currentState ->
                        currentState.copy(
                            currentPosition = controller.currentPosition,
                            duration = controller.duration.takeIf { it > 0 }
                                ?: currentState.duration,
                        )
                    }
                }
            }
        }
    }

    private fun executeSetMediaItem(music: Music) {
        val mediaItem = MediaItem.fromUri(music.uri)
        controller?.setMediaItem(mediaItem)
        controller?.prepare()
    }

    override fun setPlayList(
        musicList: List<Music>,
        startIndex: Int,
    ) {
        val currentIndex = _playlistState.value.currentIndex
        val currentMusic = _playerState.value.currentMusic

        if (currentMusic != null) {
            val restoredIndex = musicList.indexOfFirst { it.id == currentMusic.id }

            if (restoredIndex >= 0) {
                _playlistState.update {
                    it.copy(
                        playlist = musicList,
                        currentIndex = restoredIndex,
                    )
                }
                return
            }
        }

        if (currentIndex < 0 || currentIndex >= musicList.size) {
            _playlistState.update {
                it.copy(
                    playlist = musicList,
                    currentIndex = startIndex.coerceIn(0, musicList.size - 1),
                )
            }
        }
    }

    override fun setMediaItem(music: Music) {
        if (controller != null) {
            executeSetMediaItem(music)
        } else {
            controllerFeature.addListener(
                { executeSetMediaItem(music) },
                MoreExecutors.directExecutor(),
            )
        }

        _playerState.update { it.copy(currentMusic = music) }
    }

    override fun play() {
        controller?.play()
    }

    override fun pause() {
        controller?.pause()
    }

    override fun stop() {
        controller?.stop()
    }

    override fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _playerState.update {
            it.copy(currentPosition = positionMs)
        }
    }

    override fun skipForward(seconds: Long) {
        val currentPosition = controller?.currentPosition ?: 0L
        val duration = controller?.duration ?: 0L
        val newPosition = (currentPosition + seconds * 1000).coerceAtMost(duration)

        controller?.seekTo(newPosition)
        _playerState.update {
            it.copy(currentPosition = newPosition)
        }
    }

    override fun skipBackward(seconds: Long) {
        val currentPosition = controller?.currentPosition ?: 0L
        val newPosition = (currentPosition - seconds * 1000).coerceAtLeast(0L)

        controller?.seekTo(newPosition)
        _playerState.update {
            it.copy(currentPosition = newPosition)
        }
    }

    override fun skipToNext() {
        _playlistState.update { state ->
            if (state.playlist.isEmpty()) return
            state.copy(currentIndex = (state.currentIndex + 1) % state.playlist.size)
        }

        val state = _playlistState.value
        val music = state.playlist.getOrNull(state.currentIndex) ?: return

        executeSetMediaItem(music)
        _playerState.update { it.copy(currentMusic = music) }
        controller?.play()
    }

    override fun skipToPrevious() {
        _playlistState.update { state ->
            if (state.playlist.isEmpty()) return
            val newIndex = if (state.currentIndex - 1 < 0) {
                state.playlist.size - 1
            } else {
                state.currentIndex - 1
            }
            state.copy(currentIndex = newIndex)
        }

        val state = _playlistState.value
        val music = state.playlist.getOrNull(state.currentIndex) ?: return

        executeSetMediaItem(music)
        _playerState.update { it.copy(currentMusic = music) }
        controller?.play()
    }

    override fun playAtIndex(index: Int) {
        _playlistState.update { state ->
            if (state.playlist.isEmpty() || index < 0 || index >= state.playlist.size) return
            state.copy(currentIndex = index)
        }

        val state = _playlistState.value
        val music = state.playlist.getOrNull(state.currentIndex) ?: return

        executeSetMediaItem(music)
        _playerState.update { it.copy(currentMusic = music) }
        controller?.play()
    }
}

private data class PlaylistState(
    val playlist: List<Music> = emptyList(),
    val currentIndex: Int = -1,
)
