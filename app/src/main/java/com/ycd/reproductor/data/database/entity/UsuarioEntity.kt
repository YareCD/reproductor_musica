package com.ycd.reproductor.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,
    val nombre: String = "",
    val correo: String = "",
    @ColumnInfo(name = "foto_perfil")
    val fotoPerfil: String = "",
    @ColumnInfo(name = "acepto_aviso")
    val aceptoAviso: Boolean = false,

    @ColumnInfo(name = "fecha_aceptacion")
    val fechaAceptacion: Long? = null
) {
    init {
        require(nombre.length <= 50) { "El nombre no puede tener más de 50 caracteres" }
        require(correo.length <= 100) { "El correo no puede tener más de 100 caracteres" }
    }
}
