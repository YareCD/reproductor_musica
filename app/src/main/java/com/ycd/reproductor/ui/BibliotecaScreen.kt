package com.ycd.reproductor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ycd.reproductor.R
import com.ycd.reproductor.ui.theme.ReproductorTheme

@Composable
fun BibliotecaScreen(
    onCancionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var busqueda by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { EncabezadoBiblioteca() }
        item { Buscador(valor = busqueda, onCambio = { busqueda = it }) }
        item { SeccionPlaylists() }
        item { TituloCanciones(total = 24) }
        itemsIndexed(cancionesEjemplo) { indice, cancion ->
            FilaCancion(
                cancion = cancion,
                destacada = indice == 0,
                onClick = { onCancionClick(indice) }
            )
        }
    }
}

@Composable
private fun EncabezadoBiblioteca() {
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
            onClick = { },
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
        leadingIcon = {
            Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.biblioteca_menu))
        },
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
            focusedLeadingIconColor = c.onSurfaceVariant,
            unfocusedLeadingIconColor = c.onSurfaceVariant,
            focusedTrailingIconColor = c.onSurfaceVariant,
            unfocusedTrailingIconColor = c.onSurfaceVariant,
            cursorColor = c.secondary
        )
    )
}

@Composable
private fun SeccionPlaylists() {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.playlists_titulo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { }) {
                Text(
                    text = stringResource(R.string.playlists_ver_todas),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            playlistsEjemplo.forEach { playlist ->
                Column(modifier = Modifier.weight(1f)) {
                    PortadaCuadro(
                        portada = playlist.portada,
                        esquina = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(playlist.nombre),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
    destacada: Boolean,
    onClick: () -> Unit
) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PortadaCuadro(
            portada = cancion.portada,
            esquina = 12.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(cancion.titulo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (destacada) c.secondary else c.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(cancion.artista),
                style = MaterialTheme.typography.bodyMedium,
                color = c.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = formatoDuracion(cancion.duracionSeg),
            style = MaterialTheme.typography.bodyMedium,
            color = c.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E0A1A)
@Composable
private fun BibliotecaPreview() {
    ReproductorTheme {
        BibliotecaScreen(onCancionClick = { })
    }
}