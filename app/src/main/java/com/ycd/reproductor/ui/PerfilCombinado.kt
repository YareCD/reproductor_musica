package com.ycd.reproductor.ui


import com.ycd.reproductor.domain.modelos.Configuracion
import com.ycd.reproductor.domain.modelos.Usuario
import com.ycd.reproductor.domain.repository.ConfiguracionRepository
import com.ycd.reproductor.domain.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

// Usuario junto con su configuración (las dos tablas)
data class PerfilUi(
    val usuario: Usuario,
    val configuracion: Configuracion?
)

// SELECT de las dos tablas: primero el usuario y con su id se busca su configuración
@OptIn(ExperimentalCoroutinesApi::class)
fun obtenerPerfilCombinado(
    usuarioRepository: UsuarioRepository,
    configuracionRepository: ConfiguracionRepository
): Flow<PerfilUi?> =
    usuarioRepository.obtenerUsuarios()
        .map { lista -> lista.firstOrNull() }
        .flatMapLatest { usuario ->
            if (usuario == null) {
                flowOf<PerfilUi?>(null)
            } else {
                configuracionRepository.obtenerConfiguracion(usuario.idUsuario)
                    .map { config -> PerfilUi(usuario, config) }
            }
        }
