package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class ImagePhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    private var bitmap: Bitmap? = null
    private val paint: Paint = Paint().apply { isFilterBitmap = true }
    private var posX = 0f
    private var posY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = INVALID_POINTER_ID
    private val scaleDetector: ScaleGestureDetector
    private var scaleFactor = 1f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                lastTouchX = x
                lastTouchY = y
                activePointerId = event.getPointerId(0)
            }
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
            // поднял палец
            MotionEvent.ACTION_UP -> {
                activePointerId = INVALID_POINTER_ID
            }
            // если палец выходит за пределы экрана
            MotionEvent.ACTION_CANCEL -> {
                activePointerId = INVALID_POINTER_ID
            }
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
        Log.d("tag", "X: $posX Y: $posY")
        canvas.translate(posX, posY)
        canvas.scale(scaleFactor, scaleFactor)
        // todo корректно отобразить надо, исправить
        bitmap?.let { canvas.drawBitmap(it, 0f, 100f, paint) }

        canvas.restore()
    }

    // Детектор жестов масштаба
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            scaleFactor = 0.1f.coerceAtLeast(scaleFactor.coerceAtMost(10.0f))
            invalidate()
            return true
        }
    }

    init {
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
    }

    fun setData(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()
    }
}
