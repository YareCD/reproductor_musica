package com.ycd.reproductor.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ycd.reproductor.R
import com.ycd.reproductor.domain.modelos.Cancion
import com.ycd.reproductor.domain.modelos.Playlist

@Composable
fun BibliotecaScreen(
    onCancionClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BibliotecaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val canciones by viewModel.canciones.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val importadas by viewModel.importadas.collectAsState()

    var busqueda by rememberSaveable { mutableStateOf("") }
    var playlistElegida by rememberSaveable { mutableStateOf<Int?>(null) }
    var dialogoCrear by rememberSaveable { mutableStateOf(false) }
    var dialogoRenombrar by rememberSaveable { mutableStateOf(false) }
    var dialogoEliminar by rememberSaveable { mutableStateOf(false) }
    var permisoDenegado by rememberSaveable { mutableStateOf(false) }

    val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoDenegado = !concedido
        if (concedido) viewModel.importarDelTelefono()
    }
    val importar = {
        val yaTiene = ContextCompat.checkSelfPermission(context, permiso) ==
                PackageManager.PERMISSION_GRANTED
        if (yaTiene) {
            permisoDenegado = false
            viewModel.importarDelTelefono()
        } else {
            pedirPermiso.launch(permiso)
        }
    }

    val playlistActual = playlists.firstOrNull { it.idPlaylist == playlistElegida }
    val visibles = canciones.filter { cancion ->
        (playlistActual == null || cancion.idPlaylist == playlistActual.idPlaylist) &&
                (busqueda.isBlank() ||
                        cancion.titulo.contains(busqueda, ignoreCase = true) ||
                        cancion.artista.contains(busqueda, ignoreCase = true))
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { EncabezadoBiblioteca(onCrearPlaylist = { dialogoCrear = true }) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = importar, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.biblioteca_importar))
                }
                OutlinedButton(
                    onClick = { abrirYouTubeMusic(context) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.biblioteca_youtube_music))
                }
            }
        }

        if (permisoDenegado) {
            item {
                Text(
                    text = stringResource(R.string.biblioteca_permiso_denegado),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        importadas?.let { total ->
            item {
                Text(
                    text = pluralStringResource(R.plurals.biblioteca_importadas, total, total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item { Buscador(valor = busqueda, onCambio = { busqueda = it }) }

        item {
            SeccionPlaylists(
                playlists = playlists,
                elegida = playlistElegida,
                onElegir = { id -> playlistElegida = if (playlistElegida == id) null else id },
                onRenombrar = { dialogoRenombrar = true },
                onEliminar = { dialogoEliminar = true }
            )
        }

        item { TituloCanciones(total = visibles.size) }

        if (canciones.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.biblioteca_vacia),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }

        itemsIndexed(visibles, key = { _, cancion -> cancion.idCancion }) { indice, cancion ->
            FilaCancion(
                cancion = cancion,
                playlists = playlists,
                onClick = {
                    viewModel.reproducir(visibles, indice)
                    onCancionClick()
                },
                onMover = { idPlaylist -> viewModel.moverCancion(cancion, idPlaylist) },
                onEliminar = { viewModel.eliminarCancion(cancion) }
            )
        }
    }

    if (dialogoCrear) {
        DialogoNombre(
            titulo = stringResource(R.string.playlist_nueva_titulo),
            inicial = "",
            onConfirmar = {
                viewModel.crearPlaylist(it)
                dialogoCrear = false
            },
            onCancelar = { dialogoCrear = false }
        )
    }
    if (dialogoRenombrar && playlistActual != null) {
        DialogoNombre(
            titulo = stringResource(R.string.playlist_renombrar_titulo),
            inicial = playlistActual.nombre,
            onConfirmar = {
                viewModel.renombrarPlaylist(playlistActual, it)
                dialogoRenombrar = false
            },
            onCancelar = { dialogoRenombrar = false }
        )
    }
    if (dialogoEliminar && playlistActual != null) {
        AlertDialog(
            onDismissRequest = { dialogoEliminar = false },
            title = { Text(stringResource(R.string.playlist_eliminar_titulo)) },
            text = { Text(stringResource(R.string.playlist_eliminar_mensaje, playlistActual.nombre)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarPlaylist(playlistActual)
                    playlistElegida = null
                    dialogoEliminar = false
                }) {
                    Text(stringResource(R.string.accion_eliminar), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogoEliminar = false }) {
                    Text(stringResource(R.string.perfil_cancelar))
                }
            }
        )
    }
}

@Composable
private fun EncabezadoBiblioteca(onCrearPlaylist: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.biblioteca_titulo).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.5.sp
        )
        FilledIconButton(
            onClick = onCrearPlaylist,
            modifier = Modifier.size(44.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.biblioteca_crear_playlist)
            )
        }
    }
}

@Composable
private fun Buscador(valor: String, onCambio: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    TextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        placeholder = { Text(stringResource(R.string.biblioteca_buscar_hint)) },
        trailingIcon = {
            Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.biblioteca_buscar))
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = c.surface,
            unfocusedContainerColor = c.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = c.onSurface,
            unfocusedTextColor = c.onSurface,
            focusedPlaceholderColor = c.onSurfaceVariant,
            unfocusedPlaceholderColor = c.onSurfaceVariant,
            focusedTrailingIconColor = c.onSurfaceVariant,
            unfocusedTrailingIconColor = c.onSurfaceVariant,
            cursorColor = c.secondary
        )
    )
}

@Composable
private fun SeccionPlaylists(
    playlists: List<Playlist>,
    elegida: Int?,
    onElegir: (Int) -> Unit,
    onRenombrar: () -> Unit,
    onEliminar: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = stringResource(R.string.playlists_titulo),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (playlists.isEmpty()) {
            Text(
                text = stringResource(R.string.playlists_vacio),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(playlists, key = { it.idPlaylist }) { playlist ->
                    FilterChip(
                        selected = elegida == playlist.idPlaylist,
                        onClick = { onElegir(playlist.idPlaylist) },
                        label = { Text(playlist.nombre, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
        }
        if (elegida != null) {
            Row {
                TextButton(onClick = onRenombrar) {
                    Text(
                        text = stringResource(R.string.accion_renombrar),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                TextButton(onClick = onEliminar) {
                    Text(
                        text = stringResource(R.string.accion_eliminar),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun TituloCanciones(total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.canciones_titulo),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.canciones_total, total),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FilaCancion(
    cancion: Cancion,
    playlists: List<Playlist>,
    onClick: () -> Unit,
    onMover: (Int?) -> Unit,
    onEliminar: () -> Unit
) {
    val c = MaterialTheme.colorScheme
    var menuAbierto by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PortadaCuadro(
            portada = portadaDe(cancion.idCancion),
            esquina = 12.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cancion.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = c.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = cancion.artista,
                style = MaterialTheme.typography.bodyMedium,
                color = c.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = formatoDuracion((cancion.duracionMs / 1000).toInt()),
            style = MaterialTheme.typography.bodyMedium,
            color = c.onSurfaceVariant
        )
        Column {
            IconButton(onClick = { menuAbierto = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.cancion_opciones),
                    tint = c.onSurfaceVariant
                )
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                playlists.forEach { playlist ->
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.cancion_agregar_a, playlist.nombre)) },
                        onClick = {
                            onMover(playlist.idPlaylist)
                            menuAbierto = false
                        }
                    )
                }
                if (cancion.idPlaylist != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.cancion_quitar_de_playlist)) },
                        onClick = {
                            onMover(null)
                            menuAbierto = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.cancion_quitar_biblioteca),
                            color = c.error
                        )
                    },
                    onClick = {
                        onEliminar()
                        menuAbierto = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DialogoNombre(
    titulo: String,
    inicial: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by rememberSaveable { mutableStateOf(inicial) }
    val valido = texto.isNotBlank() && texto.trim().length <= 40

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                singleLine = true,
                label = { Text(stringResource(R.string.playlist_nombre_etiqueta)) },
                isError = !valido,
                supportingText = {
                    if (!valido) Text(stringResource(R.string.playlist_nombre_invalido))
                }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(texto) }, enabled = valido) {
                Text(stringResource(R.string.perfil_guardar))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text(stringResource(R.string.perfil_cancelar))
            }
        }
    )
}