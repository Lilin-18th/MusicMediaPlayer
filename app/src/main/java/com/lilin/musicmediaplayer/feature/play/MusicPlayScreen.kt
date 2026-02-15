package com.lilin.musicmediaplayer.feature.play

import android.content.ContentUris
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.ui.component.MusicPlayControl
import com.lilin.musicmediaplayer.ui.component.MusicPlayTopAppBar
import com.lilin.musicmediaplayer.ui.component.MusicProgressIndicator
import com.lilin.musicmediaplayer.ui.component.PlayerAlbumArt
import com.lilin.musicmediaplayer.ui.component.PlayerMusicInfo
import com.lilin.musicmediaplayer.ui.theme.PlayerBackgroundBottom
import com.lilin.musicmediaplayer.ui.theme.PlayerBackgroundMiddle
import com.lilin.musicmediaplayer.ui.theme.PlayerBackgroundTop
import com.lilin.musicmediaplayer.ui.theme.PlayerGlowBlue
import com.lilin.musicmediaplayer.ui.theme.PlayerGlowPurple
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.serialization.Serializable

@Serializable
data class MusicPlayScreen(
    val music: Music,
) : NavKey

@Composable
fun MusicPlayScreen(
    navKey: MusicPlayScreen,
    viewModel: MusicPlayViewModel = assistedMetroViewModel<MusicPlayViewModel, MusicPlayViewModel.Factory> {
        create(navKey.music)
    },
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MusicPlayScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onClickPlayPause = viewModel::togglePlayPause,
        onClickSkipToPrevious = viewModel::skipToPrevious,
        onClickSkipToNext = viewModel::skipToNext,
        onClickUndo = { viewModel.skipBackward(5) },
        onClickRedo = { viewModel.skipForward(5) },
        onSeek = viewModel::seekTo,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun MusicPlayScreen(
    uiState: MusicPlayUiState,
    onBackClick: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickSkipToPrevious: () -> Unit,
    onClickSkipToNext: () -> Unit,
    onClickUndo: () -> Unit,
    onClickRedo: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PlayerBackgroundTop,
                        PlayerBackgroundMiddle,
                        PlayerBackgroundBottom,
                    ),
                ),
            ),
    ) {
        PlayerGlowEffects()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val albumArtUri = remember(uiState.currentMusic.albumId) {
                ContentUris.withAppendedId(
                    "content://media/external/audio/albumart".toUri(),
                    uiState.currentMusic.albumId,
                )
            }

            MusicPlayTopAppBar(
                title = uiState.currentMusic.title,
                onBackClick = onBackClick,
            )
            Spacer(modifier = Modifier.weight(1f))

            PlayerAlbumArt(
                albumArtUri = albumArtUri,
            )
            Spacer(modifier = Modifier.height(28.dp))

            PlayerMusicInfo(
                title = uiState.currentMusic.title,
                artist = uiState.currentMusic.artist,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.weight(1f))

            MusicProgressIndicator(
                musicProgress = uiState.progress,
                currentSeconds = uiState.currentSeconds,
                totalSeconds = uiState.totalSeconds,
                onValueChange = {
                    val positionMs = (it * uiState.duration).toLong()
                    onSeek(positionMs)
                },
                onValueChangeFinished = {},
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            MusicPlayControl(
                isPlaying = uiState.isPlaying,
                onClickUndo = onClickUndo,
                onClickSkipToPrevious = onClickSkipToPrevious,
                onClickPlayPause = onClickPlayPause,
                onClickSkipToNext = onClickSkipToNext,
                onClickRedo = onClickRedo,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PlayerGlowEffects() {
    Box(
        modifier = Modifier
            .offset(x = (-20).dp, y = 120.dp)
            .size(250.dp)
            .blur(60.dp)
            .background(
                color = PlayerGlowPurple.copy(alpha = 0.2f),
                shape = CircleShape,
            ),
    )

    Box(
        modifier = Modifier
            .offset(x = 200.dp, y = 500.dp)
            .size(200.dp)
            .blur(50.dp)
            .background(
                color = PlayerGlowBlue.copy(alpha = 0.12f),
                shape = CircleShape,
            ),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayScreenPreview() {
    val music = Music(
        id = 1,
        title = "Midnight Serenade",
        artist = "Luna Orchestra",
        albumId = 1,
        duration = 208000,
        uri = "",
    )

    MusicPlayScreen(
        uiState = MusicPlayUiState(currentMusic = music),
        onBackClick = {},
        onClickPlayPause = {},
        onClickSkipToPrevious = {},
        onClickSkipToNext = {},
        onClickUndo = {},
        onClickRedo = {},
        onSeek = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

