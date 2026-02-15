package com.lilin.musicmediaplayer.feature.play.miniplayer

import android.content.ContentUris
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.ui.theme.PlayerAccent
import com.lilin.musicmediaplayer.ui.theme.PlayerSurfaceVariant

@Composable
fun MiniPlayer(
    state: MiniPlayerUiState,
    modifier: Modifier = Modifier,
    albumArtModifier: Modifier = Modifier,
    onTogglePlayPause: () -> Unit,
    onSkipToNext: () -> Unit,
    onClick: () -> Unit,
) {
    val music = state.currentMusic
    val isEnable = music != null

    val albumArtUri = remember(music?.albumId) {
        music?.albumId?.let { albumId ->
            ContentUris.withAppendedId(
                "content://media/external/audio/albumart".toUri(),
                albumId,
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PlayerSurfaceVariant.copy(alpha = 0.95f),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            )
            .then(
                if (isEnable) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            ),
    ) {
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            color = if (isEnable) PlayerAccent else Color.Transparent,
            trackColor = Color.White.copy(alpha = 0.08f),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MiniPlayerAlbumArt(
                albumArtUri = albumArtUri,
                modifier = albumArtModifier,
            )

            if (isEnable) {
                MiniPlayerInfo(
                    music = music,
                    modifier = Modifier.weight(1f),
                )
                MiniPlayerController(
                    state = state,
                    onTogglePlayPause = onTogglePlayPause,
                    onSkipToNext = onSkipToNext,
                )
            } else {
                Text(
                    text = "再生中のコンテンツはありません",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.35f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerAlbumArt(
    albumArtUri: Uri?,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(
        model = albumArtUri,
        contentScale = ContentScale.Crop,
    )
    val state by painter.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = "アルバムアート",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerInfo(
    music: Music,
    modifier: Modifier = Modifier,
    repeatDelayMillis: Int = 2000,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = music.title,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    repeatDelayMillis = repeatDelayMillis,
                ),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = music.artist,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    repeatDelayMillis = repeatDelayMillis,
                ),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.45f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MiniPlayerController(
    state: MiniPlayerUiState,
    modifier: Modifier = Modifier,
    onTogglePlayPause: () -> Unit,
    onSkipToNext: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onTogglePlayPause) {
            Icon(
                imageVector = if (state.isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = if (state.isPlaying) "一時停止" else "再生",
                modifier = Modifier.size(28.dp),
                tint = Color.White,
            )
        }

        IconButton(onClick = onSkipToNext) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "次の曲",
                modifier = Modifier.size(24.dp),
                tint = Color.White.copy(alpha = 0.5f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MiniPlayerPreview() {
    MiniPlayer(
        state = MiniPlayerUiState(
            currentMusic = Music(
                id = 1,
                title = "Midnight Serenade",
                artist = "Luna Orchestra",
                albumId = 1,
                duration = 208000,
                uri = "",
            ),
            isPlaying = true,
            progress = 0.4f,
        ),
        onTogglePlayPause = {},
        onSkipToNext = {},
        onClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MiniPlayerPausedPreview() {
    MiniPlayer(
        state = MiniPlayerUiState(
            currentMusic = Music(
                id = 1,
                title = "Very Long Song Title That Should Be Truncated With Ellipsis",
                artist = "Very Long Artist Name That Should Also Be Truncated",
                albumId = 1,
                duration = 300000,
                uri = "",
            ),
            isPlaying = false,
            progress = 0.7f,
        ),
        onTogglePlayPause = {},
        onSkipToNext = {},
        onClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MiniPlayerPlaceholderPreview() {
    MiniPlayer(
        state = MiniPlayerUiState(),
        onTogglePlayPause = {},
        onSkipToNext = {},
        onClick = {},
    )
}
