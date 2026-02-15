package com.lilin.musicmediaplayer.feature.playlist

import android.Manifest
import android.content.ContentUris
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.feature.play.MusicPlayScreen
import com.lilin.musicmediaplayer.ui.component.MusicCardItem
import com.lilin.musicmediaplayer.ui.component.TopAppBar
import com.lilin.musicmediaplayer.ui.theme.PlayerAccent
import com.lilin.musicmediaplayer.ui.theme.PlayerBackgroundBottom
import com.lilin.musicmediaplayer.ui.theme.PlayerBackgroundMiddle
import com.lilin.musicmediaplayer.ui.theme.PlayerBackgroundTop
import com.lilin.musicmediaplayer.ui.util.formatMillisToSeconds
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object MusicPlayListScreen : NavKey

internal object PlayerConfig {
    val MINI_PLAYER_HEIGHT = 58.dp
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayListScreen(
    modifier: Modifier = Modifier,
    viewModel: MusicPlayListViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerId by viewModel.currentPlayingMusicId.collectAsStateWithLifecycle()

    val storagePermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState,
    )

    val navBarPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val peekHeight = remember(navBarPadding) {
        navBarPadding + PlayerConfig.MINI_PLAYER_HEIGHT
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(storagePermissionState.status) {
        when {
            storagePermissionState.status.isGranted -> {
                viewModel.loadMusicList()
            }

            storagePermissionState.status.shouldShowRationale -> {
                storagePermissionState.launchPermissionRequest()
            }

            else -> {
                storagePermissionState.launchPermissionRequest()
            }
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            MusicPlayScreen(
                onBackClick = {},
            )
        },
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetContainerColor = Color.Transparent,
        sheetSwipeEnabled = true,
    ) { innerPadding ->
        MusicPlayListScreen(
            modifier = Modifier.padding(innerPadding),
            playerId = playerId,
            uiState = uiState,
            onMusicClick = { music ->
                val index = uiState.musicList.indexOf(music)
                if (index >= 0) {
                    viewModel.selectMusic(index)
                }
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            },
        )
    }
}

@Composable
private fun MusicPlayListScreen(
    modifier: Modifier = Modifier,
    playerId: Long?,
    uiState: PlayListUiState,
    onMusicClick: (Music) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            when {
                uiState.isLoading -> {
                    LoadingView(
                        modifier = Modifier.weight(1f),
                    )
                }

                uiState.errorMessage != null -> {
                    ErrorView(
                        errorMessage = uiState.errorMessage,
                        modifier = Modifier.weight(1f),
                    )
                }

                uiState.musicList.isEmpty() -> {
                    EmptyView(
                        modifier = Modifier.weight(1f),
                    )
                }

                else -> {
                    MusicList(
                        playerId = playerId,
                        modifier = Modifier.weight(1f),
                        musicList = uiState.musicList,
                        onMusicClick = onMusicClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No music found",
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun LoadingView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = PlayerAccent,
        )
    }
}

@Composable
private fun ErrorView(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Error: $errorMessage",
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun MusicList(
    playerId: Long?,
    musicList: List<Music>,
    modifier: Modifier = Modifier,
    onMusicClick: (Music) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TopAppBar(title = "Music Player")

        LazyColumn(
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                items = musicList,
                key = { it.id },
            ) { music ->
                val isPlaying = playerId == music.id
                val albumArtUri = remember(music.albumId) {
                    ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        music.albumId,
                    )
                }

                MusicCardItem(
                    title = music.title,
                    artist = music.artist,
                    albumArtUri = albumArtUri,
                    duration = music.duration.formatMillisToSeconds(),
                    isPlaying = isPlaying,
                    onClick = {
                        onMusicClick(music)
                    },
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayListScreenEmptyViewPreview() {
    MusicPlayListScreen(
        playerId = null,
        uiState = PlayListUiState(
            musicList = emptyList(),
            isLoading = false,
            errorMessage = null,
        ),
        onMusicClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayListScreenLoadingViewPreview() {
    MusicPlayListScreen(
        playerId = null,
        uiState = PlayListUiState(
            musicList = emptyList(),
            isLoading = true,
            errorMessage = null,
        ),
        onMusicClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayListScreenErrorViewPreview() {
    MusicPlayListScreen(
        playerId = null,
        uiState = PlayListUiState(
            musicList = emptyList(),
            isLoading = false,
            errorMessage = "Error message",
        ),
        onMusicClick = {},
    )
}
