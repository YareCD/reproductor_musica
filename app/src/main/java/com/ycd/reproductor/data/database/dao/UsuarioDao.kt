package com.ycd.reproductor.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ycd.reproductor.data.database.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE id_usuario = :id")
    fun getUsuario(id: Int): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuario ORDER BY nombre ASC")
    fun obtenerUsuarios(): Flow<List<UsuarioEntity>>
}
