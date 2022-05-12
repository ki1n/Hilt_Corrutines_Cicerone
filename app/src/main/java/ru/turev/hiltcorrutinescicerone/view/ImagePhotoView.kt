package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.appcompat.widget.AppCompatImageView
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.domain.enums.Mode
import ru.turev.hiltcorrutinescicerone.util.ImageHelper
import ru.turev.hiltcorrutinescicerone.util.extension.getCompatColor
import kotlin.math.abs


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
    private var savedMatrix = Matrix()
    private var isScaling = false
    private val focusPoint = PointF()
    private val startFocusPoint = PointF() // точка первого пальца нажата
    private val stopFocusPoint = PointF()
    private var isDrawMode = false // эта переменная для включения режима рисования
    private var isClearPatch = false // переменная для очистки того что нарисовал
    private val paintLine = Paint()
        .createStroke(color = R.color.image_photo_view_red, width = R.dimen.dp_2)
    private val points = mutableListOf<PointF>()
    private var patch = Path()
    private var mode = Mode.NONE
    private var isLoaded = false
    private lateinit var bitmap: Bitmap
    // rect: Rect = Rect(0, 0, width, height)

    private var isStart = isEventToMatrix(startFocusPoint.x, startFocusPoint.y)
    private var isStop = isEventToMatrix(stopFocusPoint.x, stopFocusPoint.y)

    // здесь можно только собирать данные
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleType = ScaleType.MATRIX
        matrixByImage.set(imageMatrix)
        savedMatrix.set(matrixByImage)
        scaleGestureDetector.onTouchEvent(event)
        if (!isScaling && scaleFactor > 1) {
            gestureDetector.onTouchEvent(event)
        }
        imageMatrix = matrixByImage

        if (isDrawMode) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_CANCEL,
                    // срабатывает при отпускании последнего пальца
                MotionEvent.ACTION_UP,
                    // срабатывает при касании первого пальца
                MotionEvent.ACTION_DOWN -> {
                    isStart = isEventToMatrix(event.x, event.y)
                    if (isStart) {
                        val startPoint = PointF(event.x, event.y)
                        points.add(startPoint)
                        mode = Mode.DRAG
                    }
                }
                // срабатывает при касании каждого последующего пальца к примеру второй
                MotionEvent.ACTION_POINTER_DOWN,
                    // Движение пальца пользователя по экрану
                MotionEvent.ACTION_MOVE -> {
                    isStart = isEventToMatrix(event.x, event.y)
                    if (!isStart) {
                        points.clear()
                        val point = PointF(event.x, event.y)
                        points.add(point)
                    }

                    if (mode == Mode.DRAG && isStart) {
                        stopFocusPoint.set(event.x, event.y)
                        val latestPoint = points.lastOrNull()
                        val point = PointF(event.x, event.y)
                        points.add(point)
                        latestPoint?.let { startFocusPoint.set(latestPoint.x, latestPoint.y) }
                    }
                }
                // срабатывает при отпускании каждого пальца кроме последнего
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = Mode.NONE
                }
            }
        }
        return true
    }

    private fun isEventToMatrix(x: Float, y: Float): Boolean {
        val values = FloatArray(9)
        savedMatrix.getValues(values)
        val topPoint = values[Matrix.MTRANS_Y] - values[Matrix.MTRANS_X]
        val lowPoint = abs(values[Matrix.MSCALE_Y] * this.height - (values[Matrix.MTRANS_Y] - values[Matrix.MTRANS_X]))
        val lowerRightPoint = values[Matrix.MSCALE_X] * this.width

        if (y in topPoint..lowPoint) {
            if (x in 0f..lowerRightPoint) {
                return true
            }
        }

        return false
    }

    private fun drawLinePatch(canvas: Canvas) {
        canvas.save()
        patch.moveTo(startFocusPoint.x, startFocusPoint.y)
        patch.lineTo(stopFocusPoint.x, stopFocusPoint.y)
        canvas.drawPath(patch, paintLine)
        patch.close()
        canvas.save()
        invalidate()
    }

    private fun clearPath() {
        if (isClearPatch) {
            patch.reset()
            invalidate()
            isClearPatch = false
            points.clear()
            startFocusPoint.set(0f, 0f)
            stopFocusPoint.set(0f, 0f)
        }
    }

    fun setIsLoadedImage(isLoaded: Boolean, bitmap: Bitmap) {
        this.isLoaded = isLoaded
        if (isLoaded) {
            try {
                this@ImagePhotoView.bitmap = bitmap
                val w = bitmap.width
                val h = bitmap.height
                Log.d("qqq", "w : $w, h : $h")
            } catch (e: Exception) {
                Log.d("qqq", "bitmap e : ${e.message}")
            }
        }
    }

    fun saveImage() {
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        ImageHelper.saveToGallery(context, bitmap, MY_ALBUM)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // todo
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // todo
    }

    override fun onDraw(canvas: Canvas) {
        clearPath()
        super.onDraw(canvas)
        drawLinePatch(canvas)
        clearPath()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (!isDrawMode) {
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
            matrixByImage.set(savedMatrix)
            matrixByImage.postScale(backScale, backScale, focusPoint.x, focusPoint.y)
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
            matrixByImage.set(savedMatrix)
            matrixByImage.postTranslate(-dx, -dy)
            checkBorders()
            setImageInVerticalCenter()
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
        this.isClearPatch = isClearPatch
    }
}
