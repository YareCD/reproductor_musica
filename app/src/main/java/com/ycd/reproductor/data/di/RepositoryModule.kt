package com.ycd.reproductor.data.di

import com.ycd.reproductor.data.repository.ConfiguracionRepositoryImp
import com.ycd.reproductor.data.repository.UsuarioRepositoryImp
import com.ycd.reproductor.domain.repository.ConfiguracionRepository
import com.ycd.reproductor.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(
        usuarioRepository: UsuarioRepositoryImp
    ): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindConfiguracionRepository(
        configuracionRepository: ConfiguracionRepositoryImp
    ): ConfiguracionRepository
}
