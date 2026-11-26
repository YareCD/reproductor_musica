package com.ycd.reproductor.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycd.reproductor.domain.modelos.Configuracion
import com.ycd.reproductor.domain.repository.ConfiguracionRepository
import com.ycd.reproductor.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MIN_BPM = 40
const val MAX_BPM = 220

@HiltViewModel
class MetronomoViewModel @Inject constructor(
    usuarioRepository: UsuarioRepository,
    private val configuracionRepository: ConfiguracionRepository
) : ViewModel() {

    // SELECT: la configuración (bpm, compás y sonido) sale de la tabla configuracion
    val configuracion: StateFlow<Configuracion?> =
        obtenerPerfilCombinado(usuarioRepository, configuracionRepository)
            .map { perfil -> perfil?.configuracion }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    private val _reproduciendo = MutableStateFlow(false)
    val reproduciendo: StateFlow<Boolean> = _reproduciendo.asStateFlow()

    private val _pulso = MutableStateFlow(0)
    val pulso: StateFlow<Int> = _pulso.asStateFlow()

    private var trabajo: Job? = null
    private var generador: ToneGenerator? = null

    // UPDATE tabla configuracion
    fun cambiarBpm(nuevo: Int) {
        val actual = configuracion.value ?: return
        val bpm = nuevo.coerceIn(MIN_BPM, MAX_BPM)
        if (bpm == actual.bpm) return
        viewModelScope.launch {
            configuracionRepository.actualizarConfiguracion(actual.copy(bpm = bpm))
        }
    }

    fun cambiarCompas(compas: String) {
        val actual = configuracion.value ?: return
        if (compas == actual.compas) return
        viewModelScope.launch {
            configuracionRepository.actualizarConfiguracion(actual.copy(compas = compas))
        }
    }

    fun alternar() {
        if (_reproduciendo.value) detener() else iniciar()
    }

    private fun iniciar() {
        if (configuracion.value == null) return
        _reproduciendo.value = true
        trabajo = viewModelScope.launch {
            var pulsoActual = 0
            var siguiente = SystemClock.elapsedRealtime()
            while (isActive) {
                val config = configuracion.value ?: break
                val tiempos = config.compas.substringBefore("/").toIntOrNull() ?: 4
                if (pulsoActual >= tiempos) pulsoActual = 0

                _pulso.value = pulsoActual
                sonar(config.sonidoMetronomo, acento = pulsoActual == 0)

                siguiente += 60_000L / config.bpm
                delay((siguiente - SystemClock.elapsedRealtime()).coerceAtLeast(0L))
                pulsoActual++
            }
            _reproduciendo.value = false
            _pulso.value = 0
        }
    }

    private fun detener() {
        trabajo?.cancel()
        trabajo = null
        _reproduciendo.value = false
        _pulso.value = 0
    }

    // Sonidos provisionales con tonos del sistema; el tiempo fuerte suena distinto
    private fun sonar(sonido: String, acento: Boolean) {
        val (fuerte, normal) = when (sonido) {
            "digital" -> ToneGenerator.TONE_DTMF_D to ToneGenerator.TONE_DTMF_A
            "suave" -> ToneGenerator.TONE_DTMF_1 to ToneGenerator.TONE_DTMF_0
            else -> ToneGenerator.TONE_DTMF_9 to ToneGenerator.TONE_DTMF_5
        }
        try {
            val tono = generador
                ?: ToneGenerator(AudioManager.STREAM_MUSIC, 100).also { generador = it }
            tono.startTone(if (acento) fuerte else normal, 50)
        } catch (e: RuntimeException) {
            // Si el sistema no puede reproducir el tono, el metrónomo sigue mostrando los pulsos
        }
    }

    override fun onCleared() {
        trabajo?.cancel()
        generador?.release()
        generador = null
        super.onCleared()
    }
}
