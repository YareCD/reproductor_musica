package com.ycd.reproductor.domain.repository

import com.ycd.reproductor.domain.modelos.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun obtenerUsuarios(): Flow<List<Usuario>>

    fun obtenerUsuario(idUsuario: Int): Flow<Usuario?>

    suspend fun insertarUsuario(usuario: Usuario): Long

    suspend fun actualizarUsuario(usuario: Usuario)

    suspend fun eliminarUsuario(usuario: Usuario)
}
