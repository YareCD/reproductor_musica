package com.ycd.reproductor.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ycd.reproductor.data.database.dao.CancionDao
import com.ycd.reproductor.data.database.dao.ConfiguracionDao
import com.ycd.reproductor.data.database.dao.PlaylistDao
import com.ycd.reproductor.data.database.dao.UsuarioDao
import com.ycd.reproductor.data.database.entity.CancionEntity
import com.ycd.reproductor.data.database.entity.ConfiguracionEntity
import com.ycd.reproductor.data.database.entity.PlaylistEntity
import com.ycd.reproductor.data.database.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ConfiguracionEntity::class,
        PlaylistEntity::class,
        CancionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun configuracionDao(): ConfiguracionDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun cancionDao(): CancionDao
}

