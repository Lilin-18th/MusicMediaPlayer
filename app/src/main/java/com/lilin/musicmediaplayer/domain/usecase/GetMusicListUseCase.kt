package com.lilin.musicmediaplayer.domain.usecase

import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.domain.repository.MusicRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class GetMusicListUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(): Flow<List<Music>> = musicRepository.getMusicList()
}
