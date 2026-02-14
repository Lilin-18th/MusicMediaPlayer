package com.lilin.musicmediaplayer.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.lilin.musicmediaplayer.feature.playlist.MusicPlayListScreen

@Composable
fun MusicPlayerApp() {
    val backStack = rememberNavBackStack(MusicPlayListScreen)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is MusicPlayListScreen -> NavEntry(key) {
                    MusicPlayListScreen(
                        onMusicClick = { backStack.add(MusicPlayListScreen) },
                    )
                }

                else -> NavEntry(key) {
                    Text(text = "Unknown screen")
                }
            }
        },
    )
}
