package com.ycd.reproductor.data.local

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.ycd.reproductor.domain.modelos.Cancion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Lee las pistas de audio que ya están en el teléfono usando MediaStore.
@Singleton
class AudioDispositivo @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun leerCanciones(): List<Cancion> = withContext(Dispatchers.IO) {
        val canciones = mutableListOf<Cancion>()
        val coleccion = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val columnas = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )
        // Solo música y de más de 30 segundos (así se evitan tonos y notas de voz)
        val filtro = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " +
            "${MediaStore.Audio.Media.DURATION} >= ?"
        val argumentos = arrayOf("30000")
        val orden = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        context.contentResolver.query(coleccion, columnas, filtro, argumentos, orden)
            ?.use { cursor ->
                val colId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val colTitulo = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val colArtista = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val colDuracion = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(colId)
                    val artista = cursor.getString(colArtista)
                    canciones.add(
                        Cancion(
                            titulo = cursor.getString(colTitulo) ?: "Sin título",
                            artista = if (artista.isNullOrBlank() || artista == "<unknown>") {
                                "Artista desconocido"
                            } else {
                                artista
                            },
                            duracionMs = cursor.getLong(colDuracion),
                            uriAudio = ContentUris.withAppendedId(coleccion, id).toString()
                        )
                    )
                }
            }
        canciones
    }
}
