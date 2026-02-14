package com.lilin.musicmediaplayer.ui.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.lilin.musicmediaplayer.ui.theme.PlayerAccent

@Composable
fun MusicCardItem(
    title: String,
    artist: String,
    duration: String,
    albumArtUri: Uri?,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isPlaying) {
                    PlayerAccent.copy(alpha = 0.12f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(12.dp),
            )
            .then(
                if (isPlaying) {
                    Modifier.border(1.dp, PlayerAccent.copy(0.2f), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ListCardAlbumArt(
            albumArtUri = albumArtUri,
        )

        MusicInfoItem(
            title = title,
            artist = artist,
            modifier = Modifier.weight(1f),
        )

        DurationText(
            duration = duration,
        )
    }
}

@Composable
private fun ListCardAlbumArt(
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
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.Gray),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
                Image(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
private fun MusicInfoItem(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = artist,
            modifier = Modifier,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.45f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DurationText(
    duration: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = duration,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(alpha = 0.35f),
    )
}

@Preview
@Composable
private fun ListCardAlbumArtPreview() {
    ListCardAlbumArt(
        albumArtUri = null,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicCardItemPreview() {
    MusicCardItem(
        title = "Music Title",
        artist = "Artist Name",
        duration = "03:45",
        albumArtUri = null,
        onClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicCardItemPlayingPreview() {
    MusicCardItem(
        title = "Music Title",
        artist = "Artist Name",
        duration = "03:45",
        isPlaying = true,
        albumArtUri = null,
        onClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0612)
@Composable
private fun MusicInfoItemPreview() {
    MusicInfoItem(
        title = "Music Title",
        artist = "Artist Name",
    )
}

