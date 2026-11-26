package com.ycd.reproductor.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ycd.reproductor.R
import com.ycd.reproductor.domain.modelos.Configuracion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MetronomoScreen(
    modifier: Modifier = Modifier,
    viewModel: MetronomoViewModel = hiltViewModel()
) {
    val configuracion by viewModel.configuracion.collectAsState()
    val reproduciendo by viewModel.reproduciendo.collectAsState()
    val pulso by viewModel.pulso.collectAsState()
    val c = MaterialTheme.colorScheme
    val config = configuracion

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.metronomo_titulo).uppercase(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            color = c.onSurfaceVariant,
            letterSpacing = 1.5.sp
        )

        if (config == null) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = stringResource(R.string.metronomo_sin_perfil),
                style = MaterialTheme.typography.bodyLarge,
                color = c.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            ContenidoMetronomo(
                config = config,
                reproduciendo = reproduciendo,
                pulso = pulso,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoMetronomo(
    config: Configuracion,
    reproduciendo: Boolean,
    pulso: Int,
    viewModel: MetronomoViewModel
) {
    val c = MaterialTheme.colorScheme
    // Valor que se ve en pantalla; se guarda en la base de datos al soltar
    var bpm by remember(config.bpm) { mutableIntStateOf(config.bpm) }
    val tiempos = config.compas.substringBefore("/").toIntOrNull() ?: 4

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotonTempo(
            icono = Icons.Filled.Remove,
            descripcion = R.string.metronomo_bajar,
            alPulsar = { bpm = (bpm - 1).coerceAtLeast(MIN_BPM) },
            alSoltar = { viewModel.cambiarBpm(bpm) }
        )
        Text(
            text = bpm.toString(),
            style = MaterialTheme.typography.displayLarge,
            color = c.onBackground
        )
        BotonTempo(
            icono = Icons.Filled.Add,
            descripcion = R.string.metronomo_subir,
            alPulsar = { bpm = (bpm + 1).coerceAtMost(MAX_BPM) },
            alSoltar = { viewModel.cambiarBpm(bpm) }
        )
    }

    Text(
        text = stringResource(R.string.metronomo_bpm),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = c.secondary,
        letterSpacing = 3.sp
    )

    Slider(
        value = bpm.toFloat(),
        onValueChange = { bpm = it.roundToInt() },
        onValueChangeFinished = { viewModel.cambiarBpm(bpm) },
        valueRange = MIN_BPM.toFloat()..MAX_BPM.toFloat(),
        modifier = Modifier.fillMaxWidth(),
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
            text = MIN_BPM.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = c.onSurfaceVariant
        )
        Text(
            text = MAX_BPM.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = c.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.metronomo_compas).uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = c.onSurfaceVariant,
        letterSpacing = 1.5.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    SingleChoiceSegmentedButtonRow {
        opcionesCompas.forEachIndexed { indice, compas ->
            SegmentedButton(
                selected = config.compas == compas,
                onClick = { viewModel.cambiarCompas(compas) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = indice,
                    count = opcionesCompas.size
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = c.primary,
                    activeContentColor = c.onPrimary,
                    activeBorderColor = c.primary,
                    inactiveContainerColor = c.surface,
                    inactiveContentColor = c.onSurface,
                    inactiveBorderColor = c.surface
                ),
                icon = { },
                label = { Text(compas) }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Un círculo por tiempo del compás; el primero (tiempo fuerte) se ilumina en amarillo
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(tiempos) { indice ->
            val activo = reproduciendo && indice == pulso
            val relleno = when {
                activo && indice == 0 -> c.tertiary
                activo -> c.primary
                else -> c.background
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(relleno)
                    .border(2.dp, if (activo) relleno else c.outline, CircleShape)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = when {
            !reproduciendo -> stringResource(R.string.metronomo_detenido)
            pulso == 0 -> stringResource(R.string.metronomo_tiempo_acento)
            else -> stringResource(R.string.metronomo_tiempo, pulso + 1)
        },
        style = MaterialTheme.typography.labelMedium,
        color = c.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { viewModel.alternar() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(
            imageVector = if (reproduciendo) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            contentDescription = stringResource(
                if (reproduciendo) R.string.metronomo_detener else R.string.metronomo_iniciar
            ),
            modifier = Modifier.size(28.dp)
        )
    }
}

// Botón redondo: un toque cambia de uno en uno y, si se mantiene presionado, sigue cambiando
@Composable
private fun BotonTempo(
    icono: ImageVector,
    @StringRes descripcion: Int,
    alPulsar: () -> Unit,
    alSoltar: () -> Unit
) {
    val c = MaterialTheme.colorScheme
    val pulsar by rememberUpdatedState(alPulsar)
    val soltar by rememberUpdatedState(alSoltar)
    val alcance = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(c.surface)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pulsar()
                        val repeticion = alcance.launch {
                            delay(400)
                            while (true) {
                                pulsar()
                                delay(70)
                            }
                        }
                        tryAwaitRelease()
                        repeticion.cancel()
                        soltar()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = stringResource(descripcion),
            tint = c.onBackground
        )
    }
}
