package com.ycd.reproductor.ui

import androidx.lifecycle.ViewModel
import com.ycd.reproductor.data.player.EstadoReproductor
import com.ycd.reproductor.data.player.ReproductorController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReproductorViewModel @Inject constructor(
    private val controller: ReproductorController
) : ViewModel() {

    val estado: StateFlow<EstadoReproductor> = controller.estado

    fun alternar() = controller.alternar()
    fun siguiente() = controller.siguiente()
    fun anterior() = controller.anterior()
    fun buscar(posicionMs: Int) = controller.buscar(posicionMs)
    fun alternarRepetir() = controller.alternarRepetir()
    fun alternarAleatorio() = controller.alternarAleatorio()
}