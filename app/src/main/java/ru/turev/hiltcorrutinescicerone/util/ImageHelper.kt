package ru.turev.hiltcorrutinescicerone.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext


object ImageHelper : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    private val CONTENT_URL: Uri
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        }

    fun saveToGallery(context: Context, bitmap: Bitmap) = launch(Dispatchers.IO) {
        try {
            val filename = "${System.currentTimeMillis()}.png"
            val write: (OutputStream) -> Unit = {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            }

            context.contentResolver.let {
                it.insert(CONTENT_URL, contentValues)
                    ?.let { uri ->
                        it.openOutputStream(uri)?.let(write)
                    }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
        }
    }
}
