package ru.turev.hiltcorrutinescicerone.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.turev.hiltcorrutinescicerone.util.extension.io
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext


object ImageHelper : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) = launch {
        io {
            try {
                val filename = "${System.currentTimeMillis()}.png"
                val write: (OutputStream) -> Boolean = {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$albumName")
                    }

                    context.contentResolver.let {
                        it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                            it.openOutputStream(uri)?.let(write)
                        }
                    }
                } else {
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .toString() + File.separator + albumName
                    val file = File(imagesDir)
                    if (!file.exists()) file.mkdir()
                    val image = File(imagesDir, filename)
                    write(FileOutputStream(image))
                }
            } catch (e: Exception) {
                Log.d("qqq", "save e : ${e.message}")
            }
        }
    }
}
