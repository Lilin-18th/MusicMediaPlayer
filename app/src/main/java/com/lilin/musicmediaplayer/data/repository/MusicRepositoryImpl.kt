package com.lilin.musicmediaplayer.data.repository

import com.lilin.musicmediaplayer.data.datasource.MusicDataSource
import com.lilin.musicmediaplayer.domain.model.Music
import com.lilin.musicmediaplayer.domain.repository.MusicRepository
import com.lilin.musicmediaplayer.data.mapper.toDomain
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class)
class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
) : MusicRepository {
    override fun getMusicList(): Flow<List<Music>> {
        return musicDataSource.getMusicList().map { list ->
            list.map { it.toDomain() }
        }
    }
}
