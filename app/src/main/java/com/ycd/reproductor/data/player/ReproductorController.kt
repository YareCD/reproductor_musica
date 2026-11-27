package com.ycd.reproductor.data.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.ycd.reproductor.domain.modelos.Cancion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class EstadoReproductor(
    val cola: List<Cancion> = emptyList(),
    val indice: Int = -1,
    val reproduciendo: Boolean = false,
    val posicionMs: Int = 0,
    val repetir: Boolean = false,
    val aleatorio: Boolean = false,
    val error: Boolean = false
) {
    val cancionActual: Cancion? get() = cola.getOrNull(indice)
}

// Singleton: una sola instancia de MediaPlayer para toda la app, así la música
// no se corta al cambiar de pestaña. Las pantallas solo observan "estado" (Observer)
// y mandan eventos con las funciones públicas (UDF).
@Singleton
class ReproductorController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _estado = MutableStateFlow(EstadoReproductor())
    val estado: StateFlow<EstadoReproductor> = _estado.asStateFlow()

    private var player: MediaPlayer? = null
    private var preparado = false
    private var ticker: Job? = null

    fun reproducir(cola: List<Cancion>, indice: Int) {
        if (indice !in cola.indices) return
        _estado.update { it.copy(cola = cola, indice = indice, error = false) }
        cargarActual()
    }

    fun alternar() {
        val mp = player ?: return
        if (!preparado) return
        if (mp.isPlaying) {
            mp.pause()
            ticker?.cancel()
            _estado.update { it.copy(reproduciendo = false) }
        } else {
            mp.start()
            iniciarTicker()
            _estado.update { it.copy(reproduciendo = true) }
        }
    }

    fun siguiente() {
        val estado = _estado.value
        if (estado.cola.isEmpty()) return
        val nuevo = if (estado.aleatorio && estado.cola.size > 1) {
            (estado.cola.indices - estado.indice).random()
        } else {
            (estado.indice + 1) % estado.cola.size
        }
        _estado.update { it.copy(indice = nuevo) }
        cargarActual()
    }

    fun anterior() {
        val estado = _estado.value
        if (estado.cola.isEmpty()) return
        // Si ya pasaron más de 3 segundos, "anterior" reinicia la canción
        if (estado.posicionMs > 3000) {
            buscar(0)
            return
        }
        val nuevo = (estado.indice - 1 + estado.cola.size) % estado.cola.size
        _estado.update { it.copy(indice = nuevo) }
        cargarActual()
    }

    fun buscar(posicionMs: Int) {
        val mp = player ?: return
        if (!preparado) return
        mp.seekTo(posicionMs)
        _estado.update { it.copy(posicionMs = posicionMs) }
    }

    fun alternarRepetir() = _estado.update { it.copy(repetir = !it.repetir) }

    fun alternarAleatorio() = _estado.update { it.copy(aleatorio = !it.aleatorio) }

    private fun cargarActual() {
        val cancion = _estado.value.cancionActual ?: return
        liberar()
        _estado.update { it.copy(posicionMs = 0, reproduciendo = false) }

        val mp = MediaPlayer()
        try {
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            mp.setDataSource(context, Uri.parse(cancion.uriAudio))
            mp.setOnPreparedListener {
                preparado = true
                it.start()
                iniciarTicker()
                _estado.update { s -> s.copy(reproduciendo = true) }
            }
            mp.setOnCompletionListener { alTerminar() }
            mp.setOnErrorListener { _, _, _ ->
                ticker?.cancel()
                _estado.update { it.copy(reproduciendo = false, error = true) }
                true
            }
            mp.prepareAsync()
            player = mp
        } catch (e: Exception) {
            mp.release()
            _estado.update { it.copy(reproduciendo = false, error = true) }
        }
    }

    private fun alTerminar() {
        val estado = _estado.value
        when {
            estado.repetir -> {
                player?.seekTo(0)
                player?.start()
            }
            estado.aleatorio || estado.indice < estado.cola.lastIndex -> siguiente()
            else -> {
                ticker?.cancel()
                _estado.update { it.copy(reproduciendo = false, posicionMs = 0) }
            }
        }
    }

    private fun iniciarTicker() {
        ticker?.cancel()
        ticker = scope.launch {
            while (isActive) {
                val mp = player
                if (mp != null && preparado) {
                    _estado.update { it.copy(posicionMs = mp.currentPosition) }
                }
                delay(500)
            }
        }
    }

    private fun liberar() {
        ticker?.cancel()
        preparado = false
        player?.release()
        player = null
    }
}
