package com.ycd.reproductor.data.repository

import com.ycd.reproductor.data.database.dao.CancionDao
import com.ycd.reproductor.data.mapper.toDomain
import com.ycd.reproductor.data.mapper.toEntity
import com.ycd.reproductor.domain.modelos.Cancion
import com.ycd.reproductor.domain.repository.CancionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancionRepositoryImp @Inject constructor(
    private val cancionDao: CancionDao
) : CancionRepository {

    override fun obtenerCanciones(): Flow<List<Cancion>> =
        cancionDao.obtenerCanciones().map { lista -> lista.map { it.toDomain() } }

    override suspend fun insertarCanciones(canciones: List<Cancion>): Int =
        cancionDao.insertarCanciones(canciones.map { it.toEntity() }).count { it != -1L }

    override suspend fun actualizarCancion(cancion: Cancion) =
        cancionDao.actualizarCancion(cancion.toEntity())

    override suspend fun eliminarCancion(cancion: Cancion) =
        cancionDao.eliminarCancion(cancion.toEntity())
}
