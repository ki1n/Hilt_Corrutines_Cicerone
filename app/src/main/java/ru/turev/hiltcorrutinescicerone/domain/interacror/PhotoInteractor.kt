package ru.turev.hiltcorrutinescicerone.domain.interacror

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import java.io.BufferedInputStream
import javax.inject.Inject


class PhotoInteractor @Inject constructor(
    private val photoRepository: PhotoRepository
) {

    suspend fun getBitmapFull(urlFull: String): Bitmap? {
        val response = photoRepository.getBitmapFull(urlFull)
        val inputStream = BufferedInputStream(response.byteStream())
        val bitmap = BitmapFactory.decodeStream(inputStream)

            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

        val bitmap2 = BitmapFactory.decodeStream(inputStream, null, options)

       // Intrinsics.checkExpressionValueIsNotNull(bitmap, "BitmapFactory.decodeStream(fis)");


//        val numOfBytes = ByteArray(inputStream.available())
//        inputStream.read(numOfBytes)
//        val options = BitmapFactory.Options()
//        options.
//        options.setExpectedImageSize(numOfBytes)
//        val bmp = BitmapFactory.decodeStream(inputStream, null, options)

        return bitmap
    }
}
