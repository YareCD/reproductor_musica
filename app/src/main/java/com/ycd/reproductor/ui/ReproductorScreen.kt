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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ycd.reproductor.R
import com.ycd.reproductor.data.player.EstadoReproductor
import com.ycd.reproductor.domain.modelos.Cancion
import com.ycd.reproductor.ui.theme.ReproductorTheme

// Pantalla con estado: obtiene el ViewModel y se lo pasa a la versión sin estado.
@Composable
fun ReproductorScreen(
    modifier: Modifier = Modifier,
    viewModel: ReproductorViewModel = hiltViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    ReproductorContenido(
        estado = estado,
        onAlternar = viewModel::alternar,
        onSiguiente = viewModel::siguiente,
        onAnterior = viewModel::anterior,
        onBuscar = viewModel::buscar,
        onRepetir = viewModel::alternarRepetir,
        onAleatorio = viewModel::alternarAleatorio,
        modifier = modifier
    )
}

@Composable
private fun ReproductorContenido(
    estado: EstadoReproductor,
    onAlternar: () -> Unit,
    onSiguiente: () -> Unit,
    onAnterior: () -> Unit,
    onBuscar: (Int) -> Unit,
    onRepetir: () -> Unit,
    onAleatorio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = MaterialTheme.colorScheme
    val context = LocalContext.current
    val cancion = estado.cancionActual
    val duracionMs = (cancion?.duracionMs ?: 0L).toInt()
    val descripcionProgreso = stringResource(R.string.reproductor_progreso)

    // Mientras el usuario arrastra el slider se muestra su posición, no la del audio
    var arrastre by remember { mutableStateOf<Float?>(null) }
    val fraccion = if (duracionMs > 0) {
        (estado.posicionMs.toFloat() / duracionMs).coerceIn(0f, 1f)
    } else {
        0f
    }
    val progreso = arrastre ?: fraccion

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.reproductor_titulo).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = c.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PortadaGrande(
            portada = if (cancion != null) portadaDe(cancion.idCancion) else Portada.Morada,
            modifier = Modifier.fillMaxWidth(0.75f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = cancion?.titulo ?: stringResource(R.string.reproductor_vacio),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 26.sp,
            color = c.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (cancion != null) {
            Text(
                text = cancion.artista,
                style = MaterialTheme.typography.bodyMedium,
                color = c.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (estado.error) {
            Text(
                text = stringResource(R.string.reproductor_error),
                style = MaterialTheme.typography.bodyMedium,
                color = c.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = progreso,
            onValueChange = { arrastre = it },
            onValueChangeFinished = {
                arrastre?.let { onBuscar((it * duracionMs).toInt()) }
                arrastre = null
            },
            enabled = cancion != null,
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
                text = formatoDuracion((progreso * duracionMs / 1000).toInt()),
                style = MaterialTheme.typography.labelMedium,
                color = c.onSurfaceVariant
            )
            Text(
                text = formatoDuracion(duracionMs / 1000),
                style = MaterialTheme.typography.labelMedium,
                color = c.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAleatorio) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = stringResource(R.string.control_aleatorio),
                    tint = if (estado.aleatorio) c.secondary else c.onSurfaceVariant
                )
            }
            IconButton(onClick = onAnterior, enabled = cancion != null) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.control_anterior),
                    tint = c.onBackground
                )
            }
            FilledIconButton(
                onClick = onAlternar,
                enabled = cancion != null,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = c.primary,
                    contentColor = c.onPrimary
                )
            ) {
                Icon(
                    imageVector = if (estado.reproduciendo) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = stringResource(
                        if (estado.reproduciendo) R.string.control_pausar else R.string.control_reproducir
                    ),
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = onSiguiente, enabled = cancion != null) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.control_siguiente),
                    tint = c.onBackground
                )
            }
            IconButton(onClick = onRepetir) {
                Icon(
                    imageVector = Icons.Filled.Repeat,
                    contentDescription = stringResource(R.string.control_repetir),
                    tint = if (estado.repetir) c.secondary else c.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { },
                border = BorderStroke(1.dp, c.outline),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
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
            OutlinedButton(
                onClick = {
                    val busqueda = if (cancion != null) "${cancion.titulo} ${cancion.artista}" else ""
                    abrirYouTubeMusic(context, busqueda)
                },
                border = BorderStroke(1.dp, c.outline),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.reproductor_youtube_music),
                    color = c.onBackground
                )
            }
        }
    }
}

@Composable
private fun PortadaGrande(portada: Portada, modifier: Modifier = Modifier) {
    val c = MaterialTheme.colorScheme
    val colorSol = c.tertiary
    val colorSombra = c.background.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(28.dp))
            .background(portada.brush())
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
        ReproductorContenido(
            estado = EstadoReproductor(
                cola = listOf(
                    Cancion(
                        idCancion = 1,
                        titulo = "Canción de ejemplo",
                        artista = "Artista de ejemplo",
                        duracionMs = 225_000L,
                        uriAudio = ""
                    )
                ),
                indice = 0,
                reproduciendo = true,
                posicionMs = 72_000
            ),
            onAlternar = { },
            onSiguiente = { },
            onAnterior = { },
            onBuscar = { },
            onRepetir = { },
            onAleatorio = { }
        )
    }
}