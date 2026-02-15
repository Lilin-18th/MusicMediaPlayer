package com.lilin.musicmediaplayer.core.service

import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicMediaSessionService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession
            .Builder(this, requireNotNull(player))
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            release()
            mediaSession = null
        }

        player?.run {
            release()
            player = null
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val mediaSessionPlayer = mediaSession?.player

        if (mediaSessionPlayer == null || !mediaSessionPlayer.playWhenReady || mediaSessionPlayer.mediaItemCount == 0) {
            mediaSessionPlayer?.run {
                stop()
                clearMediaItems()
                stopSelf()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession
}
