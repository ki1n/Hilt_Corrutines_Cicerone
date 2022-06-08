package ru.turev.hiltcorrutinescicerone.domain.interacror

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import ru.turev.hiltcorrutinescicerone.data.memory.FreeMemory
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import java.io.BufferedInputStream
import javax.inject.Inject
import kotlin.math.sqrt


class PhotoInteractor @Inject constructor(
    private val freeMemory: FreeMemory,
    private val photoRepository: PhotoRepository
) {

    suspend fun getBitmapFull(urlFull: String): Bitmap? {
        val response = photoRepository.getBitmapFull(urlFull)
        val bufferedInputStream = BufferedInputStream(response.byteStream())
        val bateArray = bufferedInputStream.readBytes().size * 4 // bate
        val bufferedMb = bateArray / 1024 / 1024
        Log.d("qqq", " bateArray:  ${bateArray}")
        Log.d("qqq", " bufferedMb:  ${bufferedMb}")
        Log.d("qqq", "freeMemory.getRemainingFreeMemory(): ${freeMemory.getRemainingFreeMemory()}")
        Log.d("qqq", "freeMemory.getRemainingFreeMemory() * 0.3) / 10: ${(freeMemory.getRemainingFreeMemory() * 0.3) / 10}")

        if (bateArray > (freeMemory.getRemainingFreeMemory() * 0.3) / 10) {
            val response2 = photoRepository.getBitmapFull(urlFull)
            val bufferedInputStream2 = BufferedInputStream(response2.byteStream())

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(bufferedInputStream2, null, options)

            val inSampleSize = calculateInSampleSize(options)
            Log.d("qqq", "inSampleSize:  $inSampleSize ")

            options.inSampleSize = inSampleSize
            Log.d("qqq", " options.inSampleSize:  ${options.inSampleSize} ")

            options.inJustDecodeBounds = false

            val response3 = photoRepository.getBitmapFull(urlFull)
            val buf3 = BufferedInputStream(response3.byteStream())

            val image = BitmapFactory.decodeStream(buf3, null, options)

            Log.d("qqq", "image:  $image ")

            return image
        } else {
            val response4 = photoRepository.getBitmapFull(urlFull)
            val bufferedInputStream4 = BufferedInputStream(response4.byteStream())
            val bitmap = BitmapFactory.decodeStream(bufferedInputStream4)
            Log.d("qqq", "bitmap bufferedInputStream4:  $bitmap")
            return bitmap
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val sizeNewBitmap = (freeMemory.getRemainingFreeMemory() * 0.3) / 10 // проверить в
        Log.d("qqq", "calculateInSampleSize sizeNewBitmap:  $sizeNewBitmap")

        val k = options.outWidth * 1f / options.outHeight * 1f
        Log.d("qqq", "k:  $k")
        //todo проверить как работает
        //   val pixelsCount = (sizeNewBitmap / 4) * 1024 * 1024 // перевод в байты
        val reqHeight = sqrt((sizeNewBitmap / k))
        Log.d("qqq", "reqHeight:  $reqHeight")
        val reqWidth = k * reqHeight
        Log.d("qqq", "reqWidth:  $reqWidth")

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        Log.d("qqq", "height:  $height")
        Log.d("qqq", "width:  $width")
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
                Log.d("qqq", "while inSampleSize:  $inSampleSize")
            }
        }

        return inSampleSize
    }

//    private fun pictureDimensionsExpected(options: BitmapFactory.Options, availableBytes: Int): Int {
//        val sizeNewBitmap = availableBytes * 0.3 // проверить в
//
//        val k = options.outWidth * 1f / options.outHeight * 1f
//        //todo
//        val pixelsCount = (sizeNewBitmap / 4) * 1024 * 1024 // перевод в байты
//
//        val newHeight = sqrt((pixelsCount / k))
//        val newWidth = k * newHeight
//
//        return calculateInSampleSize(options, newWidth.toInt(), newHeight.toInt())
//    }
}
