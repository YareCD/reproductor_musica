package com.ycd.reproductor.data.mapper

import com.ycd.reproductor.data.database.entity.ConfiguracionEntity
import com.ycd.reproductor.data.database.entity.UsuarioEntity
import com.ycd.reproductor.domain.modelos.Configuracion
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
