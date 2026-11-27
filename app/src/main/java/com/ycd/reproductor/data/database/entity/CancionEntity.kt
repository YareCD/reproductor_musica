package com.ycd.reproductor.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Una playlist tiene muchas canciones (1 a N). Si se borra la playlist,
// las canciones se quedan en la biblioteca y su id_playlist pasa a null.
@Entity(
    tableName = "cancion",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id_playlist"],
            childColumns = ["id_playlist"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["id_playlist"]),
        Index(value = ["uri_audio"], unique = true)
    ]
)
data class CancionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cancion")
    val idCancion: Int = 0,
    val titulo: String,
    val artista: String,
    @ColumnInfo(name = "duracion_ms")
    val duracionMs: Long,
    @ColumnInfo(name = "uri_audio")
    val uriAudio: String,
    @ColumnInfo(name = "id_playlist")
    val idPlaylist: Int? = null
)
