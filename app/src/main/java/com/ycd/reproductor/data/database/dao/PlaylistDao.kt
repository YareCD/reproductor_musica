package com.ycd.reproductor.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ycd.reproductor.data.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertarPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun actualizarPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun eliminarPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist ORDER BY nombre COLLATE NOCASE ASC")
    fun obtenerPlaylists(): Flow<List<PlaylistEntity>>
}
