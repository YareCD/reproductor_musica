package com.ycd.reproductor.domain.repository

import com.ycd.reproductor.domain.modelos.Configuracion
import kotlinx.coroutines.flow.Flow

interface ConfiguracionRepository {
    fun obtenerConfiguracion(idUsuario: Int): Flow<Configuracion?>

    suspend fun insertarConfiguracion(configuracion: Configuracion): Long

    suspend fun actualizarConfiguracion(configuracion: Configuracion)

    suspend fun eliminarConfiguracion(configuracion: Configuracion)
}
