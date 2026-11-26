package com.ycd.reproductor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycd.reproductor.domain.repository.ConfiguracionRepository
import com.ycd.reproductor.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Lee de la base de datos si el tema es oscuro o claro (campo tema_oscuro de configuracion)
@HiltViewModel
class TemaViewModel @Inject constructor(
    usuarioRepository: UsuarioRepository,
    configuracionRepository: ConfiguracionRepository
) : ViewModel() {

    val temaOscuro: StateFlow<Boolean> =
        obtenerPerfilCombinado(usuarioRepository, configuracionRepository)
            .map { perfil -> perfil?.configuracion?.temaOscuro ?: true }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = true
            )
}