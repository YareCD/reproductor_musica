package com.ycd.reproductor.data.mapper

import com.ycd.reproductor.data.database.entity.CancionEntity
import com.ycd.reproductor.data.database.entity.ConfiguracionEntity
import com.ycd.reproductor.data.database.entity.PlaylistEntity
import com.ycd.reproductor.data.database.entity.UsuarioEntity
import com.ycd.reproductor.domain.modelos.Cancion
import com.ycd.reproductor.domain.modelos.Configuracion
import com.ycd.reproductor.domain.modelos.Playlist
import com.ycd.reproductor.domain.modelos.Usuario

fun UsuarioEntity.toDomain(): Usuario = Usuario(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    fotoPerfil = fotoPerfil,
    aceptoAviso = aceptoAviso,
    fechaAceptacion = fechaAceptacion
)

fun Usuario.toEntity(): UsuarioEntity = UsuarioEntity(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    fotoPerfil = fotoPerfil,
    aceptoAviso = aceptoAviso,
    fechaAceptacion = fechaAceptacion
)

fun ConfiguracionEntity.toDomain(): Configuracion = Configuracion(
    idConfig = idConfig,
    idUsuario = idUsuario,
    temaOscuro = temaOscuro,
    letraAutomatica = letraAutomatica,
    sonidoMetronomo = sonidoMetronomo,
    bpm = bpm,
    compas = compas
)

fun Configuracion.toEntity(): ConfiguracionEntity = ConfiguracionEntity(
    idConfig = idConfig,
    idUsuario = idUsuario,
    temaOscuro = temaOscuro,
    letraAutomatica = letraAutomatica,
    sonidoMetronomo = sonidoMetronomo,
    bpm = bpm,
    compas = compas
)

fun PlaylistEntity.toDomain(): Playlist = Playlist(
    idPlaylist = idPlaylist,
    nombre = nombre
)

fun Playlist.toEntity(): PlaylistEntity = PlaylistEntity(
    idPlaylist = idPlaylist,
    nombre = nombre
)

fun CancionEntity.toDomain(): Cancion = Cancion(
    idCancion = idCancion,
    titulo = titulo,
    artista = artista,
    duracionMs = duracionMs,
    uriAudio = uriAudio,
    idPlaylist = idPlaylist
)

fun Cancion.toEntity(): CancionEntity = CancionEntity(
    idCancion = idCancion,
    titulo = titulo,
    artista = artista,
    duracionMs = duracionMs,
    uriAudio = uriAudio,
    idPlaylist = idPlaylist
)
