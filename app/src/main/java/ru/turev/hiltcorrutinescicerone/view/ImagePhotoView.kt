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
import kotlin.math.abs
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
    private val paintLineTest = Paint()
        .createStroke(color = R.color.purple_700, width = R.dimen.image_photo_drawing_line_thickness)
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

    private var differenceMixingMatrixX = 0f
    private var differenceMixingMatrixY = 0f

    // todo
    private var originalImageMatrixW = 0f
    private var originalImageMatrixH = 0f

    private var lastMTRANSX = 0f
    private var lastMTRANSY = 0f
    private var lastShiftCoordinates = 0f

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
                            //  val startOriginal = onPointEventToPointImage(event)
                            points.add(startPoint)
                            allPoints.add(startPoint)
                            modeTouchBehavior = true
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
                            // val startOriginal = onPointEventToPointImage(event)
                            points.add(point)
                            allPoints.add(point)
                        }

                        if (modeTouchBehavior && isPoint) {
                            val point = PointF(event.x, event.y)
                            // val startOriginal1 = onPointEventToPointImage(event)
                            stopFocusPoint.set(point)
                            val latestPoint = points.lastOrNull()
                            val pointStop = PointF(event.x, event.y)
                            // val startOriginal = onPointEventToPointImage(event)
                            points.add(pointStop)
                            allPoints.add(pointStop)
                            latestPoint?.let { startFocusPoint.set(latestPoint.x, latestPoint.y) }
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
        // onScalePointsRecalculation()

        // todo учитывать перенос
        if (scaleFactor == 1f) {
            canvas.save()
            path.moveTo(startFocusPoint.x, startFocusPoint.y)
            path.lineTo(stopFocusPoint.x, stopFocusPoint.y)
            // canvas.drawLine(startFocusPoint.x, startFocusPoint.y, stopFocusPoint.x, stopFocusPoint.y, pain)
            canvas.drawPath(path, paintLine)
            canvas.save()
        }
        if (scaleFactor > 1f) {
//            canvas.save()
//            path.moveTo(startFocusPoint.x, startFocusPoint.y)
//            path.lineTo(stopFocusPoint.x, stopFocusPoint.y)
//            canvas.drawPath(path, paintLineTest)
//            canvas.save()

            allPointsRecalculation.forEach {
              //  canvas.drawPoint(it.x - lastMTRANSX, it.y - lastMTRANSY, paintLineTest)
                 canvas.drawPoint(it.x, it.y, paintLineTest)
            }
        }
    }

    // todo функция для преобразования точки с экрана, в точку на изображении
//    private fun onPointEventToPointImage(event: MotionEvent): PointF {
//        val pointEvent = PointF(event.x, event.y)
//
////        val w = drawable.intrinsicWidth
////        val h = drawable.intrinsicHeight
////
////        val mw = measuredWidth
////        val mh = measuredHeight
////
////        val orw = originalImageMatrixW
////        val orh = originalImageMatrixH
////
////        val imageX = pointEvent.x * scaleFactor - lastMTRANSX
////        val imageY = pointEvent.y * scaleFactor
////        return PointF(imageX, imageY)
//
//
//        if (scaleFactor == 1f) {
//            val imageX = pointEvent.x
//            val imageY = pointEvent.y
//            return PointF(imageX, imageY)
//        }
//
//        if (scaleFactor > 1f) {
////            val imageX = pointEvent.x - lastMTRANSX
////            val imageY = pointEvent.y - lastMTRANSY
//            val imageX = pointEvent.x
//            val imageY = pointEvent.y
//            return PointF(imageX, imageY)
//        }
//
//        return PointF(0f, 0f)
//    }

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
            // getLastPointMatrixOffset()
            getDifferenceMixingMatrix()
            // todo
            getShiftCoordinates()
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
            getLastPointMatrixOffset()
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
            getLastPointMatrixOffset()
            return true
        }
        return false
    }

    // todo вычислить гипотенузу
    private fun getShiftCoordinates() {
        val dc =
            sqrt(abs(differenceMixingMatrixX * differenceMixingMatrixX) + abs(differenceMixingMatrixY * differenceMixingMatrixY)) // смешение матрица

        allPoints.forEach { point ->
            val x = point.x
            val y = point.y

            val fpx = focusPoint.x
            val fpy = focusPoint.y

            // треугольник от фокус поинт до нашей точки
            val fx = abs(abs(focusPoint.x) - abs(point.x))
            val fy = abs(abs(focusPoint.y) - abs(point.y))
            val fc = sqrt((fx * fx) + (fy * fy)) // растояние от фокус точки до нашей точки

            val sinY = (fy / fc) //общий угол смещения и для расширенного треугольника
            // вычисление расширенного треугольника
            val resultC = abs(dc + fc)
            val resultY = sinY * resultC
            val resultX = sqrt(abs(resultC * resultC) - abs(resultY * resultY))

            allPointsRecalculation.add(PointF(point.x + differenceMixingMatrixX, point.y + differenceMixingMatrixY))
//            allPointsRecalculation.add(
//                PointF(
//                    point.x - differenceMixingMatrixX * scaleFactor,
//                    point.y - differenceMixingMatrixY * scaleFactor
//                )
//            )
            //  allPointsRecalculation.add(PointF(focusPoint.x - resultX, focusPoint.y - resultY))
            // allPointsRecalculation.add(PointF(point.x - resultX, point.y - resultY))
            //  allPointsRecalculation.add(PointF(point.x - lastMTRANSX, point.y - lastMTRANSY))
//            if (lastMTRANSX > 0) {
//                allPointsRecalculation.add(PointF(focusPoint.x + resultX, focusPoint.y + resultY))
//            }

//            if (focusPoint.x - point.x < 0) {
//                allPointsRecalculation.add(PointF(focusPoint.x + resultX, focusPoint.y + resultY))
//            } else {
//                allPointsRecalculation.add(PointF(focusPoint.x - resultX, focusPoint.y - resultY))
//            }
        }
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

        originalImageMatrixH = getContentHeight() * 1f
        originalImageMatrixW = measuredWidth * 1f
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

    private fun getLastPointMatrixOffset() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        lastMTRANSX = abs(values[Matrix.MTRANS_X])
        lastMTRANSY = abs(values[Matrix.MTRANS_Y])
    }

    private fun getDifferenceMixingMatrix() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        differenceMixingMatrixX = abs(lastMTRANSX - abs(values[Matrix.MTRANS_X]))
        differenceMixingMatrixY = abs(lastMTRANSY - abs(values[Matrix.MTRANS_Y]))

        lastMTRANSX = abs(values[Matrix.MTRANS_X])
        lastMTRANSY = abs(values[Matrix.MTRANS_Y])
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
        setImageInVerticalCenter()
        getLastPointMatrixOffset()
    }
}
