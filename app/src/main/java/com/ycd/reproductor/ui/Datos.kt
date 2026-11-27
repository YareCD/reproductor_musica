package com.ycd.reproductor.ui

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

// Las canciones del teléfono no traen portada propia, así que a cada una
// le toca uno de los 4 degradados según su id.
fun portadaDe(idCancion: Int): Portada = Portada.entries[idCancion % Portada.entries.size]

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

@Composable
fun formatoDuracion(segundos: Int): String =
    stringResource(R.string.duracion_formato, segundos / 60, segundos % 60)