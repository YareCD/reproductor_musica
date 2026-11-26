package com.ycd.reproductor.domain.modelos

data class Configuracion(
    val idConfig: Int = 0,
    val idUsuario: Int,
    val temaOscuro: Boolean = true,
    val letraAutomatica: Boolean = false,
    val sonidoMetronomo: String = "clic",
    val bpm: Int = 120,
    val compas: String = "4/4"
)
