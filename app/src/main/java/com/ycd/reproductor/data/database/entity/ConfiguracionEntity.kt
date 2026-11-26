package com.ycd.reproductor.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "configuracion",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_usuario"], unique = true)]
)
data class ConfiguracionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_config")
    val idConfig: Int = 0,
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,
    @ColumnInfo(name = "tema_oscuro")
    val temaOscuro: Boolean = true,
    @ColumnInfo(name = "letra_automatica")
    val letraAutomatica: Boolean = false,
    @ColumnInfo(name = "sonido_metronomo")
    val sonidoMetronomo: String = "clic",
    val bpm: Int = 120,
    val compas: String = "4/4"
) {
    init {
        require(bpm in 40..220) { "El BPM debe estar entre 40 y 220" }
        require(compas in listOf("2/4", "3/4", "4/4")) { "El compás debe ser 2/4, 3/4 o 4/4" }
    }
}
