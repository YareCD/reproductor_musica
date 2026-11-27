package com.ycd.reproductor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycd.reproductor.data.local.AudioDispositivo
import com.ycd.reproductor.data.player.ReproductorController
import com.ycd.reproductor.domain.modelos.Cancion
import com.ycd.reproductor.domain.modelos.Playlist
import com.ycd.reproductor.domain.repository.CancionRepository
import com.ycd.reproductor.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_NOMBRE_PLAYLIST = 40

@HiltViewModel
class BibliotecaViewModel @Inject constructor(
    private val cancionRepository: CancionRepository,
    private val playlistRepository: PlaylistRepository,
    private val audioDispositivo: AudioDispositivo,
    private val controller: ReproductorController
) : ViewModel() {

    // SELECT
    val canciones: StateFlow<List<Cancion>> = cancionRepository.obtenerCanciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val playlists: StateFlow<List<Playlist>> = playlistRepository.obtenerPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Cuántas canciones nuevas se importaron la última vez (null = todavía no se ha importado)
    private val _importadas = MutableStateFlow<Int?>(null)
    val importadas: StateFlow<Int?> = _importadas.asStateFlow()

    // INSERT canciones: lee el audio del teléfono y lo guarda en la tabla cancion
    fun importarDelTelefono() {
        viewModelScope.launch {
            val encontradas = audioDispositivo.leerCanciones()
            _importadas.value = cancionRepository.insertarCanciones(encontradas)
        }
    }

    // INSERT playlist
    fun crearPlaylist(nombre: String) {
        val limpio = nombre.trim()
        if (limpio.isEmpty() || limpio.length > MAX_NOMBRE_PLAYLIST) return
        viewModelScope.launch {
            playlistRepository.insertarPlaylist(Playlist(nombre = limpio))
        }
    }

    // UPDATE playlist
    fun renombrarPlaylist(playlist: Playlist, nombre: String) {
        val limpio = nombre.trim()
        if (limpio.isEmpty() || limpio.length > MAX_NOMBRE_PLAYLIST) return
        viewModelScope.launch {
            playlistRepository.actualizarPlaylist(playlist.copy(nombre = limpio))
        }
    }

    // DELETE playlist (las canciones se quedan, solo pierden la playlist)
    fun eliminarPlaylist(playlist: Playlist) {
        viewModelScope.launch { playlistRepository.eliminarPlaylist(playlist) }
    }

    // UPDATE canción: meterla a una playlist o sacarla (idPlaylist = null)
    fun moverCancion(cancion: Cancion, idPlaylist: Int?) {
        viewModelScope.launch {
            cancionRepository.actualizarCancion(cancion.copy(idPlaylist = idPlaylist))
        }
    }

    // DELETE canción de la biblioteca (el archivo del teléfono no se borra)
    fun eliminarCancion(cancion: Cancion) {
        viewModelScope.launch { cancionRepository.eliminarCancion(cancion) }
    }

    fun reproducir(lista: List<Cancion>, indice: Int) = controller.reproducir(lista, indice)
}