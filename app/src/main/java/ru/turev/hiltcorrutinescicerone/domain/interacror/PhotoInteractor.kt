package ru.turev.hiltcorrutinescicerone.domain.interacror

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.turev.hiltcorrutinescicerone.data.memory.FreeMemory
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import java.io.BufferedInputStream
import javax.inject.Inject
import kotlin.math.sqrt

class PhotoInteractor @Inject constructor(
    private val photoRepository: PhotoRepository
) {

    suspend fun getBitmapFull(urlFull: String): Bitmap? {
        val secondResponse = photoRepository.getBitmapFull(urlFull)
        val bufferedInputStreamSecond = BufferedInputStream(secondResponse.byteStream())

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(bufferedInputStreamSecond, null, options)
        bufferedInputStreamSecond.close()

        val inSampleSize = calculateInSampleSize(options)

        options.inSampleSize = inSampleSize
        options.inMutable = true

        options.inJustDecodeBounds = false

        val thirdResponse = photoRepository.getBitmapFull(urlFull)
        val bufferedInputStreamThird = BufferedInputStream(thirdResponse.byteStream())

        val image = BitmapFactory.decodeStream(bufferedInputStreamThird, null, options)
        bufferedInputStreamThird.close()

        return image


        // todo tt



        
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val sizeNewBitmap = (FreeMemory.getRemainingFreeMemory() * 0.25)
        val k = options.outWidth * 1f / options.outHeight * 1f
        val reqHeight = sqrt((sizeNewBitmap / k)).toInt()
        val reqWidth = (k * reqHeight).toInt()

        var inSampleSize = 1

        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            val halfHeight: Int = options.outHeight / 2
            val halfWidth: Int = options.outWidth / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
