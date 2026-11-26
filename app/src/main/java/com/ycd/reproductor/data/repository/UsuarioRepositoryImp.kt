package com.ycd.reproductor.data.repository

import com.ycd.reproductor.data.database.dao.UsuarioDao
import com.ycd.reproductor.data.mapper.toDomain
import com.ycd.reproductor.data.mapper.toEntity
import com.ycd.reproductor.domain.modelos.Usuario
import com.ycd.reproductor.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImp @Inject constructor(
    private val usuarioDao: UsuarioDao
) : UsuarioRepository {

    override fun obtenerUsuarios(): Flow<List<Usuario>> =
        usuarioDao.obtenerUsuarios().map { lista -> lista.map { it.toDomain() } }

    override fun obtenerUsuario(idUsuario: Int): Flow<Usuario?> =
        usuarioDao.getUsuario(idUsuario).map { it?.toDomain() }

    override suspend fun insertarUsuario(usuario: Usuario): Long =
        usuarioDao.insertarUsuario(usuario.toEntity())

    override suspend fun actualizarUsuario(usuario: Usuario) =
        usuarioDao.actualizarUsuario(usuario.toEntity())

    override suspend fun eliminarUsuario(usuario: Usuario) =
        usuarioDao.eliminarUsuario(usuario.toEntity())
}
