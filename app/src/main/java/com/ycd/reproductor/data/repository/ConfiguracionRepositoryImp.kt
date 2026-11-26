package com.ycd.reproductor.data.repository

import com.ycd.reproductor.data.database.dao.ConfiguracionDao
import com.ycd.reproductor.data.mapper.toDomain
import com.ycd.reproductor.data.mapper.toEntity
import com.ycd.reproductor.domain.modelos.Configuracion
import com.ycd.reproductor.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfiguracionRepositoryImp @Inject constructor(
    private val configuracionDao: ConfiguracionDao
) : ConfiguracionRepository {

    override fun obtenerConfiguracion(idUsuario: Int): Flow<Configuracion?> =
        configuracionDao.getConfiguracionDeUsuario(idUsuario).map { it?.toDomain() }

    override suspend fun insertarConfiguracion(configuracion: Configuracion): Long =
        configuracionDao.insertarConfiguracion(configuracion.toEntity())

    override suspend fun actualizarConfiguracion(configuracion: Configuracion) =
        configuracionDao.actualizarConfiguracion(configuracion.toEntity())

    override suspend fun eliminarConfiguracion(configuracion: Configuracion) =
        configuracionDao.eliminarConfiguracion(configuracion.toEntity())
}
