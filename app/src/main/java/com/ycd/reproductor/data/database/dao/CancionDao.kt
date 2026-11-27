package com.ycd.reproductor.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ycd.reproductor.data.database.entity.CancionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CancionDao {
    // IGNORE: si la canción (misma uri_audio) ya estaba, no se duplica
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCanciones(canciones: List<CancionEntity>): List<Long>

    @Update
    suspend fun actualizarCancion(cancion: CancionEntity)

    @Delete
    suspend fun eliminarCancion(cancion: CancionEntity)

    @Query("SELECT * FROM cancion ORDER BY titulo COLLATE NOCASE ASC")
    fun obtenerCanciones(): Flow<List<CancionEntity>>
}
