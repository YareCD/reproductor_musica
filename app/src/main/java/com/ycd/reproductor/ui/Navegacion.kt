package com.ycd.reproductor.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.ycd.reproductor.R

enum class Destino(@StringRes val etiqueta: Int, val icono: ImageVector) {
    Biblioteca(R.string.nav_biblioteca, Icons.Outlined.Folder),
    Reproductor(R.string.nav_reproductor, Icons.Filled.MusicNote),
    Metronomo(R.string.nav_metronomo, Icons.Outlined.Timer),
    Perfil(R.string.nav_perfil, Icons.Outlined.AccountCircle)
}

@Composable
fun BarraNavegacion(
    destinoActual: Destino,
    onDestinoClick: (Destino) -> Unit
) {
    val c = MaterialTheme.colorScheme
    NavigationBar(containerColor = c.surface) {
        Destino.entries.forEach { destino ->
            NavigationBarItem(
                selected = destino == destinoActual,
                onClick = { onDestinoClick(destino) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = { Text(stringResource(destino.etiqueta)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = c.onBackground,
                    selectedTextColor = c.onBackground,
                    indicatorColor = c.primary.copy(alpha = 0.3f),
                    unselectedIconColor = c.onSurfaceVariant,
                    unselectedTextColor = c.onSurfaceVariant
                )
            )
        }
    }
}