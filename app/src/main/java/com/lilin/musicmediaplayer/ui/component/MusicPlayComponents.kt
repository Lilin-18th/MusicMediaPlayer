package com.lilin.musicmediaplayer.ui.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.lilin.musicmediaplayer.ui.theme.PlayerAccent
import com.lilin.musicmediaplayer.ui.theme.PlayerSurfaceVariant
import com.lilin.musicmediaplayer.ui.util.formatSeconds

@Composable
fun PlayerAlbumArt(
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
            .size(280.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(PlayerSurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = "アルバムアート",
                    modifier = Modifier.size(280.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = Color.White.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
fun PlayerMusicInfo(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MarqueeText(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        MarqueeText(
            text = artist,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.45f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    repeatDelayMillis: Int = 2000,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = textAlign,
        modifier = modifier
            .fillMaxWidth()
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                repeatDelayMillis = repeatDelayMillis,
            ),
        maxLines = 1,
    )
}

@Composable
fun MusicPlayControl(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClickUndo: () -> Unit,
    onClickSkipToPrevious: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickSkipToNext: () -> Unit,
    onClickRedo: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClickSkipToPrevious) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "前の曲",
                modifier = Modifier.size(28.dp),
                tint = Color.White.copy(alpha = 0.5f),
            )
        }

        IconButton(onClick = onClickUndo) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = "5秒戻る",
                modifier = Modifier.size(24.dp),
                tint = Color.White.copy(alpha = 0.5f),
            )
        }

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = CircleShape,
                )
                .clickable(
                    onClick = onClickPlayPause,
                    role = Role.Button,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = if (isPlaying) "一時停止" else "再生",
                modifier = Modifier.size(32.dp),
                tint = Color.White,
            )
        }

        IconButton(onClick = onClickRedo) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "5秒進む",
                modifier = Modifier.size(24.dp),
                tint = Color.White.copy(alpha = 0.5f),
            )
        }

        IconButton(onClick = onClickSkipToNext) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "次の曲",
                modifier = Modifier.size(28.dp),
                tint = Color.White.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
fun MusicProgressIndicator(
    musicProgress: Float,
    currentSeconds: Long,
    totalSeconds: Long,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Slider(
            value = musicProgress,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors(
                thumbColor = PlayerAccent,
                activeTrackColor = PlayerAccent,
                inactiveTrackColor = Color.White.copy(alpha = 0.15f),
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = currentSeconds.formatSeconds(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.35f),
            )

            Text(
                text = totalSeconds.formatSeconds(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.35f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun AlbumArtPreview() {
    PlayerAlbumArt(albumArtUri = null)
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicInfoPreview() {
    PlayerMusicInfo(
        title = "Very Long Song Title That Should Scroll Horizontally",
        artist = "Very Long Artist Name That Should Also Scroll",
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicInfoShortPreview() {
    PlayerMusicInfo(
        title = "Short Title",
        artist = "Artist",
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicProgressIndicatorPreview() {
    MusicProgressIndicator(
        musicProgress = 0.5f,
        currentSeconds = 30L,
        totalSeconds = 60L,
        onValueChange = {},
        onValueChangeFinished = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicPlayControlPausePreview() {
    MusicPlayControl(
        isPlaying = true,
        onClickUndo = {},
        onClickSkipToPrevious = {},
        onClickPlayPause = {},
        onClickSkipToNext = {},
        onClickRedo = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicPlayControlPlayPreview() {
    MusicPlayControl(
        isPlaying = false,
        onClickUndo = {},
        onClickSkipToPrevious = {},
        onClickPlayPause = {},
        onClickSkipToNext = {},
        onClickRedo = {},
    )
}
