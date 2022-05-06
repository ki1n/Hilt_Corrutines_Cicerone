package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
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
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.domain.enums.Mode
import ru.turev.hiltcorrutinescicerone.util.extension.getCompatColor

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
    private var minDrivingDistance = 1f
    private var mode = Mode.NONE

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
                // срабатывает при отпускании каждого пальца кроме последнего
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = Mode.NONE
                }
                MotionEvent.ACTION_CANCEL,
                    // срабатывает при отпускании последнего пальца
                MotionEvent.ACTION_UP,
                    // срабатывает при касании первого пальца
                MotionEvent.ACTION_DOWN -> {
                    val startPoint = PointF(event.x, event.y)
                    points.add(startPoint)
                    mode = Mode.DRAG
                }
                // срабатывает при касании каждого последующего пальца к примеру второй
                MotionEvent.ACTION_POINTER_DOWN,
                    // Движение пальца пользователя по экрану
                MotionEvent.ACTION_MOVE -> {
                    if (mode == Mode.DRAG) {
                        stopFocusPoint.set(event.x, event.y)
                        val latestPoint = points.lastOrNull()
                        val point = PointF(event.x, event.y)
                        points.add(point)
                        latestPoint?.let { startFocusPoint.set(latestPoint.x, latestPoint.y) }
                    }
                }
            }
        }
        return true
    }

    private fun drawLinePatch(canvas: Canvas) {
        canvas.save()
        // ставит «курсор» в указанную точку, рисование пойдет от нее
        patch.moveTo(
            startFocusPoint.x,
            startFocusPoint.y
        )
        // рисует линию от текущей точки до указанной, следующее рисование пойдет уже от указанной точки
        patch.lineTo(
            stopFocusPoint.x,
            stopFocusPoint.y
        )
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

//    private fun drawLineEvent(canvas: Canvas) {
//        //  canvas.save()
//        //  val values = FloatArray(9)
//        // matrixByImage.getValues(values)
//        // matrixByImage.set(savedMatrix)
//        //  canvas.translate(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y])
//        // canvas.scale(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y])
//        canvas.drawLine(startFocusPoint.x, startFocusPoint.y, stopFocusPoint.x, stopFocusPoint.y, paintLine)
//        canvas.restore()
//        invalidate()
//    }

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
                scaleFactor > maxScale -> maxScale / scaleFactor
                scaleFactor < minScale -> minScale / scaleFactor
                else -> 1F
            }
            scaleFactor = scaleFactor.coerceIn(minScale, maxScale)
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
