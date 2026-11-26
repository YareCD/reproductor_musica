package com.ycd.reproductor.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ycd.reproductor.data.database.dao.ConfiguracionDao
import com.ycd.reproductor.data.database.dao.UsuarioDao
import com.ycd.reproductor.data.database.entity.ConfiguracionEntity
import com.ycd.reproductor.data.database.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, ConfiguracionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun configuracionDao(): ConfiguracionDao
}
