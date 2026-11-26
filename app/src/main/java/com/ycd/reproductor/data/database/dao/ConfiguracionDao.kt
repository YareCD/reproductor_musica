package com.ycd.reproductor.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ycd.reproductor.data.database.entity.ConfiguracionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfiguracionDao {
    @Insert
    suspend fun insertarConfiguracion(configuracion: ConfiguracionEntity): Long

    @Update
    suspend fun actualizarConfiguracion(configuracion: ConfiguracionEntity)

    @Delete
    suspend fun eliminarConfiguracion(configuracion: ConfiguracionEntity)

    @Query("SELECT * FROM configuracion WHERE id_usuario = :idUsuario")
    fun getConfiguracionDeUsuario(idUsuario: Int): Flow<ConfiguracionEntity?>
}
