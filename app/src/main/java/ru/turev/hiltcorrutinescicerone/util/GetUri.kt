package ru.turev.hiltcorrutinescicerone.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import ru.turev.hiltcorrutinescicerone.util.constants.Constants
import java.io.File

fun getURIFile(context: Context): Uri {
    val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(Constants.PREFIX_PHOTO, Constants.MIMI_TYPE_JPG, fileDir)

    return FileProvider.getUriForFile(context, Constants.FILE_PROVIDER_AUTHORITY, file)
}


