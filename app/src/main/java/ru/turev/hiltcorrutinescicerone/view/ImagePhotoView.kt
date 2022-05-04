package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class ImagePhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener,
    GestureDetector.OnGestureListener {

    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private var scaleFactor = 1F
    private val maxScale = 3F
    private val minScale = 1F
    private var matrixByImage = Matrix()
    private var savedMatrix = Matrix()
    private var scaling = false
    private val focusPoint = PointF()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleType = ScaleType.MATRIX
        matrixByImage.set(imageMatrix)
        savedMatrix.set(matrixByImage)
        scaleGestureDetector.onTouchEvent(event)
        if (!scaling && scaleFactor > 1) {
            gestureDetector.onTouchEvent(event)
        }
        imageMatrix = matrixByImage
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        focusPoint.set(detector.focusX, detector.focusY)
        matrixByImage.set(savedMatrix)
        matrixByImage.postScale(
            detector.scaleFactor,
            detector.scaleFactor,
            focusPoint.x,
            focusPoint.y
        )
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        scaling = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        val backScale = when {
            scaleFactor > maxScale -> maxScale / scaleFactor
            scaleFactor < minScale -> minScale / scaleFactor
            else -> 1F
        }
        scaleFactor = scaleFactor.coerceIn(minScale, maxScale)
        matrixByImage.set(savedMatrix)
        matrixByImage.postScale(backScale, backScale, focusPoint.x, focusPoint.y)
        checkBorders()
        setImageInVerticalCenter()
        scaling = false
    }

    override fun onDown(p0: MotionEvent?): Boolean = true

    override fun onShowPress(p0: MotionEvent?) {
        // ignore
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean = true

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, dx: Float, dy: Float): Boolean {
        matrixByImage.set(savedMatrix)
        matrixByImage.postTranslate(-dx, -dy)
        checkBorders()
        setImageInVerticalCenter()
        return true
    }

    override fun onLongPress(p0: MotionEvent?) {
        // ignore
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = true

    private fun setImageInVerticalCenter() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)
        val dy = when {
            getContentHeight() * scaleFactor < measuredHeight ->
                0.5F * measuredHeight - values[Matrix.MTRANS_Y] - 0.5F * getContentHeight() * scaleFactor
            else -> 0F
        }
        matrixByImage.postTranslate(0F, dy)
    }

    private fun getContentHeight() =
        if (measuredHeight * drawable.intrinsicWidth <= measuredWidth * drawable.intrinsicHeight) {
            measuredHeight
        } else {
            drawable.intrinsicHeight * measuredWidth / drawable.intrinsicWidth
        }

    private fun checkBorders() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)
        val dx = when {
            values[Matrix.MTRANS_X] > 0 ->
                -values[Matrix.MTRANS_X]
            values[Matrix.MTRANS_X] < measuredWidth - measuredWidth * scaleFactor ->
                -values[Matrix.MTRANS_X] + (measuredWidth - measuredWidth * scaleFactor)
            else -> 0F
        }
        val dy = when {
            values[Matrix.MTRANS_Y] > 0 -> {
                -values[Matrix.MTRANS_Y]
            }
            values[Matrix.MTRANS_Y] + getContentHeight() * scaleFactor < measuredHeight -> {
                measuredHeight - (values[Matrix.MTRANS_Y] + getContentHeight() * scaleFactor)
            }
            else -> 0F
        }
        matrixByImage.postTranslate(dx, dy)
    }
}
