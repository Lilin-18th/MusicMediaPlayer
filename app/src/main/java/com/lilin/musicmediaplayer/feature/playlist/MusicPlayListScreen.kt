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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.derivedStateOf
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
import com.lilin.musicmediaplayer.feature.play.miniplayer.MiniPlayer
import com.lilin.musicmediaplayer.feature.play.miniplayer.MiniPlayerUiState
import com.lilin.musicmediaplayer.feature.play.miniplayer.MiniPlayerViewModel
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
    miniPlayerViewModel: MiniPlayerViewModel = metroViewModel(),
) {
    // ViewModels
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val miniPlayerState by miniPlayerViewModel.miniPlayerUiState.collectAsStateWithLifecycle()
    val playerId by viewModel.currentPlayingMusicId.collectAsStateWithLifecycle()
    // Permissions
    val storagePermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    // Permissions Side Effects
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

    MusicPlayListScreen(
        modifier = modifier,
        playerId = playerId,
        uiState = uiState,
        miniPlayerState = miniPlayerState,
        onMusicClick = { music ->
            val index = uiState.musicList.indexOf(music)
            if (index >= 0) {
                viewModel.selectMusic(index)
            }
        },
        onTogglePlayPause = miniPlayerViewModel::toggleMiniPlayerPause,
        onSkipToNext = miniPlayerViewModel::miniPlayerSkipToNext,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MusicPlayListScreen(
    modifier: Modifier = Modifier,
    playerId: Long?,
    uiState: PlayListUiState,
    miniPlayerState: MiniPlayerUiState,
    onMusicClick: (Music) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipToNext: () -> Unit,
) {
    // BottomSheet UI States
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState,
    )
    val scope = rememberCoroutineScope()
    // BottomSheet UI Calculations
    val navBarPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val peekHeight = remember(navBarPadding) {
        navBarPadding + PlayerConfig.MINI_PLAYER_HEIGHT
    }
    // BottomSheet Derived States
    val isExpanded by remember {
        derivedStateOf {
            when {
                bottomSheetState.targetValue == SheetValue.Expanded -> true
                bottomSheetState.currentValue == SheetValue.Expanded &&
                    bottomSheetState.targetValue == SheetValue.PartiallyExpanded -> false

                else -> false
            }
        }
    }
    val hasCurrentMusic = miniPlayerState.currentMusic != null

    BottomSheetScaffold(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
            ) {
                if (isExpanded && hasCurrentMusic) {
                    MusicPlayScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        onCollapseClick = {
                            scope.launch { bottomSheetState.partialExpand() }
                        },
                    )
                } else {
                    MiniPlayer(
                        state = miniPlayerState,
                        albumArtModifier = Modifier,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxHeight(),
                        onTogglePlayPause = onTogglePlayPause,
                        onSkipToNext = onSkipToNext,
                        onClick = {
                            scope.launch { bottomSheetState.expand() }
                        },
                    )
                }
            }
        },
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContainerColor = Color.Transparent,
        sheetDragHandle = null,
        sheetSwipeEnabled = hasCurrentMusic,
    ) {
        Box(
            modifier = Modifier
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
                            onMusicClick = {
                                onMusicClick(it)
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                        )
                    }
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
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp + PlayerConfig.MINI_PLAYER_HEIGHT,
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
        miniPlayerState = MiniPlayerUiState(),
        onMusicClick = {},
        onTogglePlayPause = {},
        onSkipToNext = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayListScreenViewPreview() {
    val musicList = listOf(
        Music(
            id = 1,
            title = "Midnight Serenade",
            artist = "Luna Orchestra",
            albumId = 1,
            duration = 208000,
            uri = "",
        ),
        Music(
            id = 2,
            title = "Midnight Serenade",
            artist = "Luna Orchestra",
            albumId = 1,
            duration = 308000,
            uri = "",
        ),
        Music(
            id = 3,
            title = "Midnight Serenade",
            artist = "Luna Orchestra",
            albumId = 1,
            duration = 408000,
            uri = "",
        ),
    )

    MusicPlayListScreen(
        playerId = null,
        uiState = PlayListUiState(
            musicList = musicList,
            isLoading = false,
            errorMessage = null,
        ),
        miniPlayerState = MiniPlayerUiState(),
        onMusicClick = {},
        onTogglePlayPause = {},
        onSkipToNext = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayListScreenMiniPlayerViewPreview() {
    val musicList = listOf(
        Music(
            id = 1,
            title = "Midnight Serenade",
            artist = "Luna Orchestra",
            albumId = 1,
            duration = 208000,
            uri = "",
        ),
        Music(
            id = 2,
            title = "Midnight Serenade",
            artist = "Luna Orchestra",
            albumId = 1,
            duration = 308000,
            uri = "",
        ),
        Music(
            id = 3,
            title = "Midnight Serenade",
            artist = "Luna Orchestra",
            albumId = 1,
            duration = 408000,
            uri = "",
        ),
    )

    MusicPlayListScreen(
        playerId = null,
        uiState = PlayListUiState(
            musicList = musicList,
            isLoading = false,
            errorMessage = null,
        ),
        miniPlayerState = MiniPlayerUiState(
            currentMusic = musicList[0],
            isPlaying = true,
            progress = 0.7f,
        ),
        onMusicClick = {},
        onTogglePlayPause = {},
        onSkipToNext = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun MusicPlayListScreenLoadingViewPreview() {
    MusicPlayListScreen(
        playerId = null,
        uiState = PlayListUiState(
            musicList = emptyList(),
            isLoading = false,
            errorMessage = null,
        ),
        miniPlayerState = MiniPlayerUiState(),
        onMusicClick = {},
        onTogglePlayPause = {},
        onSkipToNext = {},
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
        miniPlayerState = MiniPlayerUiState(),
        onMusicClick = {},
        onTogglePlayPause = {},
        onSkipToNext = {},
    )
}
