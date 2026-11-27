package com.ycd.reproductor.domain.modelos

data class Cancion(
    val idCancion: Int = 0,
    val titulo: String,
    val artista: String,
    val duracionMs: Long,
    val uriAudio: String,
    val idPlaylist: Int? = null
)
