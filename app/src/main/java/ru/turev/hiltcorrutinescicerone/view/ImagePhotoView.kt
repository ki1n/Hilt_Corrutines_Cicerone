package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withMatrix
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.util.ImageHelper
import ru.turev.hiltcorrutinescicerone.util.extension.getCompatColor


class ImagePhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener,
    GestureDetector.OnGestureListener {

    companion object {
        private const val MAX_SCALE = 5f
        private const val MIN_SCALE = 1f
        private const val MY_ALBUM = "albumName"
    }

    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private var scaleFactor = 1f
    private var matrixByImage = Matrix()
    private var savedByImageMatrix = Matrix()
    private var isScaling = false
    private val focusPoint = PointF()
    private var isDrawMode = false
    private val paintLine = Paint()
        .createStroke(color = R.color.image_photo_view_red, width = R.dimen.image_photo_drawing_line_thickness)

    private val points = mutableListOf<PointF>()
    private var path = Path()

    private var allPoints = HashMap<MyPath, Paint>()

    private var myPath = MyPath()
    private var matrixByPoints = Matrix()

    private var startPointX = 0f
    private var startPointY = 0f
    private var currentX = 0f
    private var currentY = 0f

    private var modeTouchBehavior = false
    private var topPoint = 0f
    private var lowPoint = 0f
    private var lowerRightPoint = 0f
    private var isZoomImage = false

    private var differenceMixingMatrixX = 0f
    private var differenceMixingMatrixY = 0f
    private var lastMTRANSX = 0f
    private var lastMTRANSY = 0f
    private var defaultMTRANSY = 0f
    private var defaultMTRANSX = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isZoomImage) {
            scaleType = ScaleType.MATRIX
            matrixByImage.set(imageMatrix)
            savedByImageMatrix.set(matrixByImage)

            scaleGestureDetector.onTouchEvent(event)
            if (!isScaling && scaleFactor > 1) gestureDetector.onTouchEvent(event)

            imageMatrix = matrixByImage

            val values: FloatArray = floatArrayOf(event.x, event.y)
            matrixByPoints.mapPoints(values)
            val x = values[0]
            val y = values[1]

            if (isDrawMode) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (isEventToMatrix(event.x, event.y)) {
                            startPointX = x
                            startPointY = y
                            actionDown(x, y)
                            modeTouchBehavior = true
                        }
                    }
                    MotionEvent.ACTION_MOVE -> if (isEventToMatrix(event.x, event.y) && modeTouchBehavior) {
                        actionMove(x, y)
                    }

                    MotionEvent.ACTION_UP -> if (isEventToMatrix(event.x, event.y)) actionUp()
                }
            }
            invalidate()
            return true
        } else {
            return false
        }
    }

    private fun actionDown(x: Float, y: Float) {
        myPath.reset()
        myPath.moveTo(x, y)
        currentX = x
        currentY = y
    }

    private fun actionMove(x: Float, y: Float) {
        myPath.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2)
        currentX = x
        currentY = y
    }

    private fun actionUp() {
        myPath.lineTo(currentX, currentY)

        if (startPointX == currentX && startPointY == currentY) {
            myPath.lineTo(currentX, currentY + 2)
            myPath.lineTo(currentX + 1, currentY + 2)
            myPath.lineTo(currentX + 1, currentY)
        }

        allPoints[myPath] = paintLine
    }

    private fun updateDataValuesMatrix() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        if (scaleFactor == 1f) {
            topPoint = values[Matrix.MTRANS_Y] - values[Matrix.MTRANS_X]
            lowPoint = values[Matrix.MSCALE_Y] * measuredHeight - (values[Matrix.MTRANS_Y] - values[Matrix.MTRANS_X])
            lowerRightPoint = values[Matrix.MSCALE_X] * measuredWidth
        }

        if (scaleFactor > 1f) {
            topPoint = values[Matrix.MTRANS_Y]
            lowPoint = measuredHeight - (values[Matrix.MTRANS_Y])
            lowerRightPoint = values[Matrix.MSCALE_X] * measuredWidth + values[Matrix.MTRANS_X]
        }
    }

    private fun isEventToMatrix(x: Float, y: Float): Boolean {
        updateDataValuesMatrix()

        if (y in topPoint..lowPoint) {
            if (x in 0f..lowerRightPoint) return true
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLinePatch(canvas)
    }

    private fun drawLinePatch(canvas: Canvas) {
        canvas.withMatrix(matrixByPoints) {
            for ((key, value) in allPoints) {
                canvas.drawPath(key, paintLine)
            }
        }
    }

    private fun clearPath() {
        myPath.close()
        allPoints.clear()
        invalidate()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (!isDrawMode) {
            scaleFactor *= detector.scaleFactor
            focusPoint.set(measuredWidth / 2 * 1f, measuredHeight / 2 * 1f)
            matrixByImage.set(savedByImageMatrix)

            matrixByImage.postScale(
                detector.scaleFactor,
                detector.scaleFactor,
                focusPoint.x,
                focusPoint.y
            )
            matrixByPoints.postScale(detector.scaleFactor, detector.scaleFactor, focusPoint.x, focusPoint.y)

            invalidate()
            return true
        }
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        isScaling = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        if (!isDrawMode) {
            val backScale = when {
                scaleFactor > MAX_SCALE -> MAX_SCALE / scaleFactor
                scaleFactor < MIN_SCALE -> MIN_SCALE / scaleFactor
                else -> 1F
            }
            scaleFactor = scaleFactor.coerceIn(MIN_SCALE, MAX_SCALE)
            matrixByImage.set(savedByImageMatrix)
            matrixByImage.postScale(backScale, backScale, measuredWidth / 2 * 1f, measuredHeight / 2 * 1f)
            checkBorders()
            setImageInVerticalCenter()

            isScaling = false
        }
    }

    override fun onDown(p0: MotionEvent?): Boolean = true

    override fun onShowPress(p0: MotionEvent?) {
        // ignore
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean = true

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, dx: Float, dy: Float): Boolean {
        if (!isDrawMode) {
            matrixByImage.set(savedByImageMatrix)
            matrixByImage.postTranslate(-dx, -dy)
            matrixByPoints.postTranslate(-dx, -dy)
            checkBorders()
            setImageInVerticalCenter()
            invalidate()
            return true
        }
        return false
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
        matrixByPoints.postTranslate(0f, dy)
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
        matrixByPoints.postTranslate(dx, dy)
    }

    fun saveImage() {
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        ImageHelper.saveToGallery(context, bitmap, MY_ALBUM)
    }

    private fun Paint.createStroke(@ColorRes color: Int, @DimenRes width: Int) = this.apply {
        isAntiAlias = true
        this.color = context.getCompatColor(color)
        strokeWidth = resources.getDimension(width)
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }

    fun setIsDrawMode(isDrawMode: Boolean) {
        this.isDrawMode = isDrawMode
    }

    fun setIsClearPatch(isClearPatch: Boolean) {
        if (isClearPatch) clearPath()
    }

    fun setAllowZoomImage(isZoomImage: Boolean) {
        this.isZoomImage = isZoomImage
    }
}
