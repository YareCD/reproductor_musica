package com.ycd.reproductor

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ycd.reproductor.ui.BarraNavegacion
import com.ycd.reproductor.ui.BibliotecaScreen
import com.ycd.reproductor.ui.Destino
import com.ycd.reproductor.ui.MetronomoScreen
import com.ycd.reproductor.ui.PerfilScreen
import com.ycd.reproductor.ui.ReproductorScreen
import com.ycd.reproductor.ui.TemaViewModel
import com.ycd.reproductor.ui.cancionesEjemplo
import com.ycd.reproductor.ui.theme.ReproductorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // El tema (claro u oscuro) sale de la base de datos: campo tema_oscuro
            val temaViewModel: TemaViewModel = hiltViewModel()
            val temaOscuro by temaViewModel.temaOscuro.collectAsState()

            // Iconos de la barra de estado claros u oscuros según el tema
            DisposableEffect(temaOscuro) {
                this@MainActivity.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { temaOscuro },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { temaOscuro }
                )
                onDispose { }
            }

            ReproductorTheme(darkTheme = temaOscuro) {
                AppReproductor()
            }
        }
    }
}

@Composable
fun AppReproductor() {
    var destino by rememberSaveable { mutableStateOf(Destino.Biblioteca) }
    var indiceCancion by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BarraNavegacion(
                destinoActual = destino,
                onDestinoClick = { destino = it }
            )
        }
    ) { innerPadding ->
        // El Scaffold ya incluye el espacio de la barra de estado en innerPadding
        val modifier = Modifier.padding(innerPadding)

        when (destino) {
            Destino.Biblioteca -> BibliotecaScreen(
                onCancionClick = { indice ->
                    indiceCancion = indice
                    destino = Destino.Reproductor
                },
                modifier = modifier
            )

            Destino.Reproductor -> ReproductorScreen(
                cancion = cancionesEjemplo[indiceCancion],
                modifier = modifier
            )

            Destino.Metronomo -> MetronomoScreen(modifier = modifier)

            Destino.Perfil -> PerfilScreen(modifier = modifier)
        }
    }
}