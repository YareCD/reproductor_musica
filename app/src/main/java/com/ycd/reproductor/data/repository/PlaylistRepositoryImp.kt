package com.ycd.reproductor.data.repository

import com.ycd.reproductor.data.database.dao.PlaylistDao
import com.ycd.reproductor.data.mapper.toDomain
import com.ycd.reproductor.data.mapper.toEntity
import com.ycd.reproductor.domain.modelos.Playlist
import com.ycd.reproductor.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImp @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun obtenerPlaylists(): Flow<List<Playlist>> =
        playlistDao.obtenerPlaylists().map { lista -> lista.map { it.toDomain() } }

    override suspend fun insertarPlaylist(playlist: Playlist): Long =
        playlistDao.insertarPlaylist(playlist.toEntity())

    override suspend fun actualizarPlaylist(playlist: Playlist) =
        playlistDao.actualizarPlaylist(playlist.toEntity())

    override suspend fun eliminarPlaylist(playlist: Playlist) =
        playlistDao.eliminarPlaylist(playlist.toEntity())
}
