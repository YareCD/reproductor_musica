package com.ycd.reproductor.data.di

import android.content.Context
import androidx.room.Room
import com.ycd.reproductor.data.database.AppDatabase
import com.ycd.reproductor.data.database.dao.ConfiguracionDao
import com.ycd.reproductor.data.database.dao.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "music_toolbox.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao = database.usuarioDao()

    @Provides
    fun provideConfiguracionDao(database: AppDatabase): ConfiguracionDao =
        database.configuracionDao()
}
