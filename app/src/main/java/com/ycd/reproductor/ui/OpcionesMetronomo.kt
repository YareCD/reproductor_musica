package com.ycd.reproductor.ui

import androidx.annotation.StringRes
import com.ycd.reproductor.R

data class OpcionSonido(
    val id: String,
    @StringRes val etiqueta: Int
)

val opcionesSonido = listOf(
    OpcionSonido("clic", R.string.sonido_clic),
    OpcionSonido("digital", R.string.sonido_digital),
    OpcionSonido("suave", R.string.sonido_suave)
)

val opcionesCompas = listOf("2/4", "3/4", "4/4")