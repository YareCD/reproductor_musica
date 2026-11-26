package com.ycd.reproductor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaOscuro = darkColorScheme(
    primary = Acento,
    onPrimary = Color.White,
    primaryContainer = Azul,
    onPrimaryContainer = Color.White,
    secondary = AcentoClaro,
    onSecondary = Fondo,
    secondaryContainer = Verde,
    onSecondaryContainer = Color.White,
    tertiary = Resalte,
    onTertiary = Fondo,
    tertiaryContainer = Naranja,
    onTertiaryContainer = Color.White,
    error = Rojo,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = Superficie,
    onSurfaceVariant = TextoSecundario,
    surfaceContainer = Superficie,
    outline = TextoSecundario
)

private val EsquemaClaro = lightColorScheme(
    primary = Acento,
    onPrimary = Color.White,
    primaryContainer = Azul,
    onPrimaryContainer = Color.White,
    secondary = AcentoOscuro,
    onSecondary = Color.White,
    secondaryContainer = Verde,
    onSecondaryContainer = Color.White,
    tertiary = Resalte,
    onTertiary = Fondo,
    tertiaryContainer = Naranja,
    onTertiaryContainer = Color.White,
    error = Rojo,
    background = FondoClaro,
    onBackground = TextoPrincipalClaro,
    surface = SuperficieClaro,
    onSurface = TextoPrincipalClaro,
    surfaceVariant = SuperficieClaro,
    onSurfaceVariant = TextoSecundarioClaro,
    surfaceContainer = SuperficieClaro,
    outline = TextoSecundarioClaro
)

@Composable
fun ReproductorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
