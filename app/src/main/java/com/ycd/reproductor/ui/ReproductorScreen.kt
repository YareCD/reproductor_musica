package com.ycd.reproductor.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ycd.reproductor.R
import com.ycd.reproductor.ui.theme.ReproductorTheme

@Composable
fun ReproductorScreen(
    cancion: Cancion,
    modifier: Modifier = Modifier
) {
    var reproduciendo by rememberSaveable { mutableStateOf(false) }
    var progreso by rememberSaveable { mutableStateOf(0.32f) }
    val c = MaterialTheme.colorScheme
    val descripcionProgreso = stringResource(R.string.reproductor_progreso)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reproductor_titulo).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = c.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
            FilledIconButton(
                onClick = { },
                modifier = Modifier.size(44.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = c.surface,
                    contentColor = c.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.reproductor_opciones)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Portada grande
        PortadaGrande(modifier = Modifier.fillMaxWidth(0.75f))

        Spacer(modifier = Modifier.height(28.dp))

        // Título y artista
        Text(
            text = stringResource(cancion.titulo),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 26.sp,
            color = c.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(cancion.artista),
            style = MaterialTheme.typography.bodyMedium,
            color = c.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Barra de progreso
        Slider(
            value = progreso,
            onValueChange = { progreso = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = descripcionProgreso },
            colors = SliderDefaults.colors(
                thumbColor = c.onBackground,
                activeTrackColor = c.primary,
                inactiveTrackColor = c.surface
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatoDuracion((progreso * cancion.duracionSeg).toInt()),
                style = MaterialTheme.typography.labelMedium,
                color = c.onSurfaceVariant
            )
            Text(
                text = formatoDuracion(cancion.duracionSeg),
                style = MaterialTheme.typography.labelMedium,
                color = c.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = stringResource(R.string.control_aleatorio),
                    tint = c.onSurfaceVariant
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.control_anterior),
                    tint = c.onBackground
                )
            }
            FilledIconButton(
                onClick = { reproduciendo = !reproduciendo },
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = c.primary,
                    contentColor = c.onPrimary
                )
            ) {
                Icon(
                    imageVector = if (reproduciendo) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = stringResource(
                        if (reproduciendo) R.string.control_pausar else R.string.control_reproducir
                    ),
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.control_siguiente),
                    tint = c.onBackground
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Repeat,
                    contentDescription = stringResource(R.string.control_repetir),
                    tint = c.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ver letra
        OutlinedButton(
            onClick = { },
            border = BorderStroke(1.dp, c.outline),
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = c.onBackground
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.reproductor_ver_letra),
                color = c.onBackground
            )
        }
    }
}

@Composable
private fun PortadaGrande(modifier: Modifier = Modifier) {
    val c = MaterialTheme.colorScheme
    val colorSol = c.tertiary
    val colorSombra = c.background.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(28.dp))
            .background(Portada.Morada.brush())
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = colorSombra,
                radius = size.minDimension * 0.3f,
                center = Offset(size.width * 0.18f, size.height * 0.92f)
            )
            drawCircle(
                color = colorSol,
                radius = size.minDimension * 0.11f,
                center = Offset(size.width * 0.78f, size.height * 0.2f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E0A1A)
@Composable
private fun ReproductorPreview() {
    ReproductorTheme {
        ReproductorScreen(cancion = cancionesEjemplo.first())
    }
}