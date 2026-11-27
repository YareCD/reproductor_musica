package com.ycd.reproductor.domain.repository

import com.ycd.reproductor.domain.modelos.Cancion
import kotlinx.coroutines.flow.Flow

interface CancionRepository {
    fun obtenerCanciones(): Flow<List<Cancion>>

    // Regresa cuántas canciones nuevas se guardaron (las repetidas se ignoran)
    suspend fun insertarCanciones(canciones: List<Cancion>): Int

    suspend fun actualizarCancion(cancion: Cancion)

    suspend fun eliminarCancion(cancion: Cancion)
}
