package com.ycd.reproductor.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycd.reproductor.domain.modelos.Configuracion
import com.ycd.reproductor.domain.modelos.Usuario
import com.ycd.reproductor.domain.repository.ConfiguracionRepository
import com.ycd.reproductor.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed interface PerfilEstado {
    object Cargando : PerfilEstado
    object SinPerfil : PerfilEstado
    data class ConPerfil(val perfil: PerfilUi) : PerfilEstado
}

@HiltViewModel
class PerfilViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usuarioRepository: UsuarioRepository,
    private val configuracionRepository: ConfiguracionRepository
) : ViewModel() {

    // SELECT: el usuario junto con su configuración (las dos tablas)
    val estado: StateFlow<PerfilEstado> =
        obtenerPerfilCombinado(usuarioRepository, configuracionRepository)
            .map { perfil ->
                if (perfil == null) PerfilEstado.SinPerfil else PerfilEstado.ConPerfil(perfil)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PerfilEstado.Cargando
            )

    private fun perfilActual(): PerfilUi? = (estado.value as? PerfilEstado.ConPerfil)?.perfil

    // INSERT: crea el usuario y, con el id que le tocó, su configuración
    fun crearPerfil() {
        viewModelScope.launch {
            val idNuevo = usuarioRepository.insertarUsuario(Usuario())
            configuracionRepository.insertarConfiguracion(
                Configuracion(idUsuario = idNuevo.toInt())
            )
        }
    }

    // UPDATE tabla usuario: nombre y correo
    fun guardarPerfil(nombre: String, correo: String) {
        val actual = perfilActual() ?: return
        viewModelScope.launch {
            usuarioRepository.actualizarUsuario(
                actual.usuario.copy(
                    nombre = nombre.trim().take(50),
                    correo = correo.trim().take(100)
                )
            )
        }
    }

    // UPDATE tabla usuario: foto de perfil (se copia al almacenamiento de la app)
    fun cambiarFoto(uri: Uri) {
        val actual = perfilActual() ?: return
        viewModelScope.launch {
            val ruta = copiarFoto(uri) ?: return@launch
            usuarioRepository.actualizarUsuario(actual.usuario.copy(fotoPerfil = ruta))
            borrarArchivo(actual.usuario.fotoPerfil)
        }
    }

    // DELETE: borra el usuario y, por el CASCADE, también su configuración
    fun eliminarPerfil() {
        val actual = perfilActual() ?: return
        viewModelScope.launch {
            usuarioRepository.eliminarUsuario(actual.usuario)
            borrarArchivo(actual.usuario.fotoPerfil)
        }
    }

    // UPDATE tabla configuracion
    fun cambiarTemaOscuro(activado: Boolean) {
        val config = perfilActual()?.configuracion ?: return
        viewModelScope.launch {
            configuracionRepository.actualizarConfiguracion(config.copy(temaOscuro = activado))
        }
    }

    fun cambiarLetraAutomatica(activada: Boolean) {
        val config = perfilActual()?.configuracion ?: return
        viewModelScope.launch {
            configuracionRepository.actualizarConfiguracion(config.copy(letraAutomatica = activada))
        }
    }

    fun cambiarSonido(sonido: String) {
        val config = perfilActual()?.configuracion ?: return
        viewModelScope.launch {
            configuracionRepository.actualizarConfiguracion(config.copy(sonidoMetronomo = sonido))
        }
    }

    private suspend fun copiarFoto(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val destino = File(context.filesDir, "foto_perfil_${System.currentTimeMillis()}.jpg")
            val copiado = context.contentResolver.openInputStream(uri)?.use { entrada ->
                destino.outputStream().use { salida -> entrada.copyTo(salida) }
            }
            if (copiado == null) null else destino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun borrarArchivo(ruta: String) {
        if (ruta.isBlank()) return
        withContext(Dispatchers.IO) { File(ruta).delete() }
    }
}
