package com.ycd.reproductor.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ycd.reproductor.R

private const val PAQUETE_YOUTUBE_MUSIC = "com.google.android.apps.youtube.music"

// Abre YouTube Music con un Intent. Si la app está instalada se abre ahí;
// si no, se abre music.youtube.com en el navegador.
fun abrirYouTubeMusic(context: Context, busqueda: String = "") {
    val uri = if (busqueda.isBlank()) {
        Uri.parse("https://music.youtube.com")
    } else {
        Uri.parse("https://music.youtube.com/search")
            .buildUpon()
            .appendQueryParameter("q", busqueda)
            .build()
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(Intent(intent).setPackage(PAQUETE_YOUTUBE_MUSIC))
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(intent)
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(context, R.string.youtube_no_disponible, Toast.LENGTH_SHORT).show()
        }
    }
}