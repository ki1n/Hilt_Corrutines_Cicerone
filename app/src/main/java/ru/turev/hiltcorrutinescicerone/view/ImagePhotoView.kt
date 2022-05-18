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
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.util.ImageHelper
import ru.turev.hiltcorrutinescicerone.util.extension.getCompatColor
import kotlin.math.floor
import kotlin.math.sqrt


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
    private val paintLine = Paint()
        .createStroke(color = R.color.image_photo_view_red, width = R.dimen.image_photo_drawing_line_thickness)
    private val points = mutableListOf<PointF>()
    private var path = Path()
    private var modeTouchBehavior = false
    private val allPoints = mutableListOf<PointF>()
    private val allPointsRecalculation = mutableListOf<PointF>()
    private val allPointTransform = mutableListOf<PointF>()

    private var topPoint = 0f
    private var lowPoint = 0f
    private var lowerRightPoint = 0f
    private var isZoomImage = false

    private var changeCoordinatesScaleFactorX = 0f
    private var changeCoordinatesScaleFactorY = 0f

    private var moveToAxisX = 0f
    private var moveToAxisY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isZoomImage) {
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
                        val isPoint = isEventToMatrix(event.x, event.y)
                        if (isPoint) {
                            val startPoint = PointF(event.x, event.y)
                            points.add(startPoint)
                            allPoints.add(startPoint)
                            modeTouchBehavior = true
                            //todo
                            // getOriginalCoordinateXY()
                        }
                    }
                    // срабатывает при касании каждого последующего пальца к примеру второй
                    MotionEvent.ACTION_POINTER_DOWN,
                        // Движение пальца пользователя по экрану
                    MotionEvent.ACTION_MOVE -> {
                        val isPoint = isEventToMatrix(event.x, event.y)
                        if (!isPoint) {
                            points.clear()
                            val point = PointF(event.x, event.y)
                            points.add(point)
                            allPoints.add(point)
                            //todo
                            //getOriginalCoordinateXY()
                        }

                        if (modeTouchBehavior && isPoint) {
                            val point = PointF(event.x, event.y)
                            stopFocusPoint.set(point)
                            val latestPoint = points.lastOrNull()
                            val pointStop = PointF(event.x, event.y)
                            points.add(pointStop)
                            allPoints.add(pointStop)
                            latestPoint?.let { startFocusPoint.set(latestPoint.x, latestPoint.y) }
                            // todo
                            // getOriginalCoordinateXY()
                        }
                    }
                    // срабатывает при отпускании каждого пальца кроме последнего
                    MotionEvent.ACTION_POINTER_UP -> {
                        modeTouchBehavior = false
                    }
                }
            }
            invalidate()
            return true
        } else {
            return false
        }
    }

    private fun onScalePointsRecalculation() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        if (scaleFactor > 1f) {
            allPoints.forEach { point ->
                val pointMatrixInvert = getTransformPointInMatrix(point)
                val absolutPointWidthHeight = getAbsolutePositionWidthheight(point)
                val absolutPointMeasuredWidth = getAbsolutePositionMeasuredWidth(point)

                val pointx = point.x
                val pointy = point.y

                val newX3 = point.x - changeCoordinatesScaleFactorX
                val newY3 = point.y - changeCoordinatesScaleFactorY

                allPointsRecalculation.add(pointMatrixInvert)
            }
        }
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    // todo не успел привести в порядок функцию когда в ручную высчитывается мидл точка между пальцами. В других вариантах матрица, фокус сама высчитывае видимо
//    fun getAbsolutePosition(Ax: Float, Ay: Float): FloatArray? {
//        val fromAxToBxInCanvasSpace: Float = (mCenterScaleX - Ax) / scaleFactor
//        val fromBxToCanvasEdge: Float = mCanvasWidth - Bx
//        val x: Float = mCanvasWidth - fromAxToBxInCanvasSpace - fromBxToCanvasEdge
//
//        val fromAyToByInCanvasSpace: Float = (mCenterScaleY - Ay) / scaleFactor
//        val fromByToCanvasEdge: Float = mCanvasHeight - By
//        val y: Float = mCanvasHeight - fromAyToByInCanvasSpace - fromByToCanvasEdge
//
//        return floatArrayOf(x, y)
//    }
//
//
//    fun getAbsolutePosition(Ax: Float, Ay: Float): FloatArray? {
//        val x: Float = getAbsolutePosition(mBx, Ax)
//        val y: Float = getAbsolutePosition(mBy, Ay)
//        return floatArrayOf(x, y)
//    }

    // todo получение абсолютных координат через матрицу
    private fun getTransformPointInMatrix(pointF: PointF): PointF {
        val invertMatrix = Matrix()
        imageMatrix.invert(invertMatrix)
        val values = floatArrayOf(pointF.x, pointF.y)
        invertMatrix.mapPoints(values)
        return PointF(floor(values[0]), floor(values[1]))
    }

    // todo получение абсолютных координат относительно размеров всего экрана
    private fun getAbsolutePositionMeasuredWidth(pointF: PointF): PointF {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        val x: Float =
            this.measuredWidth - (values[Matrix.MTRANS_X] - pointF.x) / values[Matrix.MSCALE_X] - (this.measuredWidth - translationX)
        val y: Float =
            this.measuredHeight - (values[Matrix.MTRANS_Y] - pointF.y) / values[Matrix.MSCALE_X] - (this.measuredHeight - translationY)
        return PointF(x, y)
    }

    // todo получение абсолютных координат относительно размеров самой вью
    private fun getAbsolutePositionWidthheight(pointF: PointF): PointF {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        val x: Float =
            this.width - (values[Matrix.MTRANS_X] - pointF.x) / values[Matrix.MSCALE_X] - (this.width - translationX)
        val y: Float =
            this.height - (values[Matrix.MTRANS_Y] - pointF.y) / values[Matrix.MSCALE_X] - (this.height - translationY)
        return PointF(x, y)
    }

    private fun updateDataValuesMatrix() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        if (scaleFactor == 1f) {
            topPoint = values[Matrix.MTRANS_Y]
            lowPoint = values[Matrix.MSCALE_Y] * this.height - (values[Matrix.MTRANS_Y])
            lowerRightPoint = values[Matrix.MSCALE_X] * this.width
        }

        if (scaleFactor > 1f) {
            topPoint = values[Matrix.MTRANS_Y]
            lowPoint = this.height - (values[Matrix.MTRANS_Y])
            lowerRightPoint = values[Matrix.MSCALE_X] * this.width + values[Matrix.MTRANS_X]
        }
    }

    private fun isEventToMatrix(x: Float, y: Float): Boolean {
        updateDataValuesMatrix()

        if (y in topPoint..lowPoint) {
            if (x in 0f..lowerRightPoint) return true
        }
        return false
    }

    private fun drawLinePatch(canvas: Canvas) {
        onScalePointsRecalculation()

        // todo учитывать перенос
        if (scaleFactor == 1f) {
            path.moveTo(startFocusPoint.x, startFocusPoint.y)
            path.lineTo(stopFocusPoint.x, stopFocusPoint.y)
            canvas.drawPath(path, paintLine)
        } else {
            allPointsRecalculation.forEach {
                canvas.drawPoint(it.x, it.y, paintLine)
            }
        }
    }

    private fun clearPath() {
        val zeroPoint = PointF(0f, 0f)
        path.reset()
        path.close()
        points.clear()
        allPoints.clear()
        allPointTransform.clear()
        allPointsRecalculation.clear()
        startFocusPoint.set(zeroPoint)
        stopFocusPoint.set(zeroPoint)
        invalidate()
    }

    fun saveImage() {
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        ImageHelper.saveToGallery(context, bitmap, MY_ALBUM)
    }

    fun setAllowZoomImage(isZoomImage: Boolean) {
        this.isZoomImage = isZoomImage
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLinePatch(canvas)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (!isDrawMode) {
            scaleFactor *= detector.scaleFactor
            focusPoint.set(detector.focusX, detector.focusY)
            matrixByImage.set(savedMatrix)
            matrixByImage.postScale(detector.scaleFactor, detector.scaleFactor, focusPoint.x, focusPoint.y)
            //todo
            // onScalePointsRecalculation()
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
            // todo
            //onScalePointsRecalculation()
            changeCoordinatesScaleFactorX = dx
            changeCoordinatesScaleFactorY = dy

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
        if (isClearPatch) clearPath()
    }
}
