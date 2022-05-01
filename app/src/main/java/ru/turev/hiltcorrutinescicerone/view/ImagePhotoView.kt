package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import ru.turev.hiltcorrutinescicerone.R

class ImagePhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener
// GestureDetector.OnGestureListener
{

    private var bitmap: Bitmap? = null
    // private var matrix = Matrix()

    private val paint: Paint = Paint().apply { isFilterBitmap = true }
    private val rect = Rect(0, 0, 0, 0)
    private var posX = 0f
    private var posY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = INVALID_POINTER_ID
    private val scaleDetector = ScaleGestureDetector(context, this)

    // private val gestureDetector = GestureDetector(context, this)
    private var scaleFactor = 1f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            // Жест начинается с события движения, ACTION_DOWN которое указывает местоположение первого указателя вниз
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                lastTouchX = x
                lastTouchY = y
                activePointerId = event.getPointerId(0)
            }
            // Движения указателя описываются событиями движения с помощью
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)

                if (!scaleDetector.isInProgress) {
                    val dx = x - lastTouchX
                    val dy = y - lastTouchY
                    posX += dx
                    posY += dy
                    invalidate()
                }
                lastTouchX = x
                lastTouchY = y
            }
//            // поднял палец
            MotionEvent.ACTION_UP -> {
                activePointerId = INVALID_POINTER_ID
            }
            // если палец выходит за пределы экрана
            MotionEvent.ACTION_CANCEL -> {
                activePointerId = INVALID_POINTER_ID
            }
            // При перемещении каждого дополнительного указателя вниз или вверх
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = (event.action and MotionEvent.ACTION_POINTER_INDEX_MASK
                        shr MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(posX, posY)
        // canvas.scale(scaleFactor, scaleFactor) //увеличиват с начальных точек
        canvas.scale(scaleFactor, scaleFactor, width / 2 * 1f, height / 2 * 1f) // увеличивает с центра
        bitmap?.let { canvas.drawBitmap(it, rect.left.toFloat(), rect.bottom.toFloat(), paint) }
        canvas.restore()
    }

    private fun Bitmap.getPointF(rect: Rect): PointF {
        return PointF()
    }

    private fun Canvas.drawBitmap(
        bitmap: Bitmap,
        point: PointF,
        paint: Paint? = null
    ) = drawBitmap(bitmap, point.x, point.y, paint)

    // Детектор жестов маштаба
//    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            scaleFactor *= detector.scaleFactor
//
//            scaleFactor = 0.1f.coerceAtLeast(scaleFactor.coerceAtMost(10.0f))
//            invalidate()
//            return true
//        }
//    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        // scaleDetector = ScaleGestureDetector(context, ScaleListener())
        onBackground()
    }

    private fun onBackground() {
        if (bitmap == null) {
            this.background = resources.getDrawable(R.drawable.ic_placeholder_image_photo, null)
        } else {
            this.background = null
        }
    }

    fun setData(bitmap: Bitmap) {
        val newBitmap = scaleImage(bitmap, width, height)
        this.bitmap = newBitmap
        onBackground()
        invalidate()
    }

    // функция маштабирует изображение
    private fun scaleImage(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        // todo высчет размеров вытягивает изображение
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    override fun onScale(p0: ScaleGestureDetector?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScaleEnd(p0: ScaleGestureDetector?) {
        TODO("Not yet implemented")
    }
}
