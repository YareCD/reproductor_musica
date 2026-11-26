package com.ycd.reproductor.domain.modelos

data class Usuario(
    val idUsuario: Int = 0,
    val nombre: String = "",
    val correo: String = "",
    val fotoPerfil: String = "",
    val aceptoAviso: Boolean = false,
    val fechaAceptacion: Long? = null
)
