package com.ycd.reproductor.domain.repository

import com.ycd.reproductor.domain.modelos.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun obtenerPlaylists(): Flow<List<Playlist>>

    suspend fun insertarPlaylist(playlist: Playlist): Long

    suspend fun actualizarPlaylist(playlist: Playlist)

    suspend fun eliminarPlaylist(playlist: Playlist)
}
