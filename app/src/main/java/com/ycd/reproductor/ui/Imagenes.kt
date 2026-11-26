package com.ycd.reproductor.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// Carga la foto guardada en el celular, en tamaño reducido para no gastar memoria,
// y la gira si la cámara la guardó de lado
fun cargarBitmap(ruta: String, ladoMaximo: Int): ImageBitmap? {
    return try {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, medidas)

        var muestra = 1
        while (medidas.outWidth / muestra > ladoMaximo * 2 ||
            medidas.outHeight / muestra > ladoMaximo * 2
        ) {
            muestra *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        val original = BitmapFactory.decodeFile(ruta, opciones) ?: return null

        val grados = when (
            ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        val resultado = if (grados == 0f) {
            original
        } else {
            val matriz = Matrix().apply { postRotate(grados) }
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
        }
        resultado.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
