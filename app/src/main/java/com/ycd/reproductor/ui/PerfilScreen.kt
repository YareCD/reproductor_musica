package com.ycd.reproductor.ui

import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ycd.reproductor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    // La vista se conecta al ViewModel: los datos salen de la base de datos local
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.perfil_titulo).uppercase(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (val actual = estado) {
            PerfilEstado.Cargando -> Unit
            PerfilEstado.SinPerfil -> SinPerfil(onCrear = { viewModel.crearPerfil() })
            is PerfilEstado.ConPerfil -> ContenidoPerfil(perfil = actual.perfil, viewModel = viewModel)
        }
    }
}

@Composable
private fun SinPerfil(onCrear: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Spacer(modifier = Modifier.height(24.dp))
    Icon(
        imageVector = Icons.Outlined.AccountCircle,
        contentDescription = null,
        modifier = Modifier.size(96.dp),
        tint = c.outline
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = stringResource(R.string.perfil_sin_perfil),
        style = MaterialTheme.typography.bodyLarge,
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(20.dp))
    Button(onClick = onCrear) {
        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(stringResource(R.string.perfil_crear))
    }
}

@Composable
private fun ContenidoPerfil(perfil: PerfilUi, viewModel: PerfilViewModel) {
    val c = MaterialTheme.colorScheme
    val usuario = perfil.usuario
    val config = perfil.configuracion

    var editando by rememberSaveable { mutableStateOf(false) }
    var nombreEditado by rememberSaveable { mutableStateOf("") }
    var correoEditado by rememberSaveable { mutableStateOf("") }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    var mostrarSonidos by rememberSaveable { mutableStateOf(false) }

    // Selector de fotos del sistema
    val selectorFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.cambiarFoto(uri)
    }
    val abrirSelector = {
        selectorFoto.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    FotoPerfil(ruta = usuario.fotoPerfil, onClick = abrirSelector)
    TextButton(onClick = abrirSelector) {
        Text(text = stringResource(R.string.perfil_cambiar_foto), color = c.secondary)
    }

    Spacer(modifier = Modifier.height(4.dp))

    if (editando) {
        val correoValido = correoEditado.isBlank() ||
                Patterns.EMAIL_ADDRESS.matcher(correoEditado.trim()).matches()

        OutlinedTextField(
            value = nombreEditado,
            onValueChange = { nombreEditado = it.take(50) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.perfil_nombre_etiqueta)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = correoEditado,
            onValueChange = { correoEditado = it.take(100) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !correoValido,
            label = { Text(stringResource(R.string.perfil_correo_etiqueta)) },
            supportingText = {
                if (!correoValido) Text(stringResource(R.string.perfil_correo_invalido))
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { editando = false },
                border = BorderStroke(1.dp, c.outline)
            ) {
                Text(text = stringResource(R.string.perfil_cancelar), color = c.onBackground)
            }
            Button(
                enabled = correoValido,
                onClick = {
                    viewModel.guardarPerfil(nombreEditado, correoEditado)
                    editando = false
                }
            ) {
                Text(stringResource(R.string.perfil_guardar))
            }
        }
    } else {
        Text(
            text = if (usuario.nombre.isBlank()) {
                stringResource(R.string.perfil_nombre_vacio)
            } else {
                usuario.nombre
            },
            style = MaterialTheme.typography.titleLarge,
            color = c.onBackground
        )
        Text(
            text = if (usuario.correo.isBlank()) {
                stringResource(R.string.perfil_correo_vacio)
            } else {
                usuario.correo
            },
            style = MaterialTheme.typography.bodyMedium,
            color = c.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                nombreEditado = usuario.nombre
                correoEditado = usuario.correo
                editando = true
            },
            border = BorderStroke(1.dp, c.outline)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = c.onBackground
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(R.string.perfil_editar), color = c.onBackground)
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(
        text = stringResource(R.string.config_titulo),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = c.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (config != null) {
        val sonidoActual = opcionesSonido.firstOrNull { it.id == config.sonidoMetronomo }
            ?: opcionesSonido.first()

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = c.surface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                FilaSwitch(
                    titulo = R.string.config_tema_oscuro,
                    activado = config.temaOscuro,
                    onCambio = { viewModel.cambiarTemaOscuro(it) }
                )
                FilaSwitch(
                    titulo = R.string.config_letra_automatica,
                    activado = config.letraAutomatica,
                    onCambio = { viewModel.cambiarLetraAutomatica(it) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarSonidos = true }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.config_sonido),
                        style = MaterialTheme.typography.bodyLarge,
                        color = c.onSurface
                    )
                    Text(
                        text = stringResource(sonidoActual.etiqueta),
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.onSurfaceVariant
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = { confirmarEliminar = true },
        border = BorderStroke(1.dp, c.error)
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = c.error
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = stringResource(R.string.perfil_eliminar), color = c.error)
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            containerColor = c.surface,
            title = { Text(stringResource(R.string.perfil_eliminar_titulo)) },
            text = { Text(stringResource(R.string.perfil_eliminar_mensaje)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarPerfil()
                        confirmarEliminar = false
                    }
                ) {
                    Text(text = stringResource(R.string.perfil_eliminar), color = c.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text(stringResource(R.string.perfil_cancelar))
                }
            }
        )
    }

    if (mostrarSonidos && config != null) {
        AlertDialog(
            onDismissRequest = { mostrarSonidos = false },
            containerColor = c.surface,
            title = { Text(stringResource(R.string.config_sonido)) },
            text = {
                Column {
                    opcionesSonido.forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.cambiarSonido(opcion.id)
                                    mostrarSonidos = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = config.sonidoMetronomo == opcion.id,
                                onClick = {
                                    viewModel.cambiarSonido(opcion.id)
                                    mostrarSonidos = false
                                }
                            )
                            Text(stringResource(opcion.etiqueta))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarSonidos = false }) {
                    Text(stringResource(R.string.perfil_cancelar))
                }
            }
        )
    }
}

@Composable
private fun FotoPerfil(ruta: String, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = ruta) {
        value = if (ruta.isBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) { cargarBitmap(ruta, 512) }
        }
    }
    val imagen = bitmap

    Box(
        modifier = Modifier
            .size(112.dp)
            .clip(CircleShape)
            .background(c.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = stringResource(R.string.perfil_avatar),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = stringResource(R.string.perfil_avatar),
                modifier = Modifier.size(112.dp),
                tint = c.secondary
            )
        }
    }
}

@Composable
private fun FilaSwitch(
    @StringRes titulo: Int,
    activado: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titulo),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = activado, onCheckedChange = onCambio)
    }
}
