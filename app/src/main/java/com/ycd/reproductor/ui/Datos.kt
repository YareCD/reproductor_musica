package com.ycd.reproductor.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.ycd.reproductor.R

enum class Portada { Morada, Azul, Naranja, Verde }

@Composable
fun Portada.brush(): Brush {
    val c = MaterialTheme.colorScheme
    val colores = when (this) {
        Portada.Morada -> listOf(c.primary, c.secondary)
        Portada.Azul -> listOf(c.primaryContainer, c.primary)
        Portada.Naranja -> listOf(c.tertiaryContainer, c.error)
        Portada.Verde -> listOf(c.secondaryContainer, c.primaryContainer)
    }
    return Brush.linearGradient(colores)
}

@Composable
fun PortadaCuadro(portada: Portada, esquina: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(esquina))
            .background(portada.brush())
    )
}

data class Cancion(
    @StringRes val titulo: Int,
    @StringRes val artista: Int,
    val duracionSeg: Int,
    val portada: Portada
)

data class Playlist(
    @StringRes val nombre: Int,
    val portada: Portada
)

val cancionesEjemplo = listOf(
    Cancion(R.string.cancion_1_titulo, R.string.cancion_1_artista, 225, Portada.Morada),
    Cancion(R.string.cancion_2_titulo, R.string.cancion_2_artista, 242, Portada.Azul),
    Cancion(R.string.cancion_3_titulo, R.string.cancion_3_artista, 178, Portada.Naranja),
    Cancion(R.string.cancion_4_titulo, R.string.cancion_4_artista, 210, Portada.Verde)
)

val playlistsEjemplo = listOf(
    Playlist(R.string.playlist_favoritas, Portada.Azul),
    Playlist(R.string.playlist_estudiar, Portada.Naranja),
    Playlist(R.string.playlist_entrenar, Portada.Verde)
)

@Composable
fun formatoDuracion(segundos: Int): String =
    stringResource(R.string.duracion_formato, segundos / 60, segundos % 60)