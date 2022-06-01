package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
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

    //  private var mScaleGestureDetector: ScaleGestureDetector
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private var scaleFactor = 1f
    private var deltaScaleFactor = 1f
    private var matrixByImage = Matrix()
    private var savedByImageMatrix = Matrix()
    private var isScaling = false
    private val focusPoint = PointF()
    private val startFocusPoint = PointF() // точка первого пальца нажата
    private val stopFocusPoint = PointF()
    private var isDrawMode = false // эта переменная для включения режима рисования
    private val paintLine = Paint()
        .createStroke(color = R.color.image_photo_view_red, width = R.dimen.image_photo_drawing_line_thickness)

    private val points = mutableListOf<PointF>()
    private var path = Path()

    private var allPoints = HashMap<MyPath, Paint>()
    private var mLastPaths = HashMap<MyPath, Paint>()
    private var mUndonePaths = HashMap<MyPath, Paint>()

    private var mPath = MyPath()
    private var matrixByPoints = Matrix()
    private var saveByPointsMatrix = Matrix()

    private var mBackground: Bitmap? = null
    private var mBackgroundRect = RectF()
    private var mIsScrolling = false

    private var mScrollOriginX = 0f
    private var mScrollOriginY = 0f
    private var mScrollX = 0f
    private var mScrollY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mCurX = 0f
    private var mCurY = 0f
    private var isScale = false
    private var isScroll = false

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

    private fun getPointerCenter(event: MotionEvent): MotionEvent.PointerCoords {
        val result = MotionEvent.PointerCoords()
        val temp = MotionEvent.PointerCoords()
        for (i in 0 until event.pointerCount) {
            event.getPointerCoords(i, temp)
            result.x += temp.x
            result.y += temp.y
        }

        if (event.pointerCount > 0) {
            result.x /= event.pointerCount.toFloat()
            result.y /= event.pointerCount.toFloat()
        }

        return result
    }

    private fun handleScroll(event: MotionEvent): Boolean {
        // todo MotionEvent.ACTION_MOVE
        if (event.action != MotionEvent.ACTION_UP
            && event.action != MotionEvent.ACTION_DOWN
            && event.action != MotionEvent.ACTION_POINTER_DOWN
            && event.action != MotionEvent.ACTION_POINTER_UP
            && event.action != MotionEvent.ACTION_MOVE
        )
            return false
        // if (event.action == MotionEvent.ACTION_MOVE) {
        val shouldScroll = event.pointerCount < 1
        val center = getPointerCenter(event)
        if (shouldScroll != mIsScrolling) {
            mUndonePaths.clear()
            mPath.reset()
            if (shouldScroll) {
                mIsScrolling = true
                mScrollOriginX = center.x
                mScrollOriginY = center.y
            } else if (event.action == MotionEvent.ACTION_UP)
                mIsScrolling = false
            return true
        }
        if (shouldScroll) {
            mScrollX += (center.x - mScrollOriginX) / scaleFactor
            mScrollY += (center.y - mScrollOriginY) / scaleFactor
            mScrollOriginX = center.x
            mScrollOriginY = center.y
            invalidate()
        }
        return mIsScrolling
        //}
        // return mIsScrolling
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isZoomImage) {
            scaleType = ScaleType.MATRIX
            matrixByImage.set(imageMatrix)
            savedByImageMatrix.set(matrixByImage)

            // todo
            // matrixByPoints.set(imageMatrix)
            // saveByPointsMatrix.set(matrixByPoints)

            scaleGestureDetector.onTouchEvent(event)
            if (!isScaling && scaleFactor > 1) gestureDetector.onTouchEvent(event)

            imageMatrix = matrixByImage
            //todo
            //imageMatrix = matrixByPoints

            val points2: FloatArray = floatArrayOf(event.x, event.y)
            matrixByPoints.mapPoints(points2)
            saveByPointsMatrix.set(matrixByPoints)
            val x = points2[0]
            val y = points2[1]

            if (isDrawMode) {
                // if (handleScroll(event)) return true
                when (event.action) {
                    // срабатывает при касании первого пальца
                    MotionEvent.ACTION_DOWN -> {
                        if (isEventToMatrix(event.x, event.y)) {
                            mStartX = x
                            mStartY = y
                            actionDown(x, y)
                            modeTouchBehavior = true
                            // mUndonePaths.clear()
                        }
                    }
                    // Движение пальца пользователя по экрану
                    MotionEvent.ACTION_MOVE -> if (isEventToMatrix(event.x, event.y) && modeTouchBehavior)
                        actionMove(x, y)
                    // срабатывает при отпускании последнего пальца
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
        mPath.reset()
        mPath.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    private fun actionMove(x: Float, y: Float) {
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        mPath.lineTo(mCurX, mCurY)

        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY)
        }

        allPoints[mPath] = paintLine
        mPath = MyPath()
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

    private fun drawLinePatch(canvas: Canvas) {
        // todo
        // mTransform.setTranslate(mScrollX, mScrollY)
        // mTransform.setTranslate(mScrollX + canvas.clipBounds.centerX(), mScrollY + canvas.clipBounds.centerY())

        canvas.withMatrix(matrixByPoints) {
            // matrixByPoints.invert(matrixByPoints)

            for ((key, value) in allPoints) {
                canvas.drawPath(key, paintLine)
            }
            // canvas.drawPath(mPath, paintLine)
        }
    }

    private fun clearPath() {
        val zeroPoint = PointF(0f, 0f)
        path.reset()
        path.close()
        points.clear()
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLinePatch(canvas)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (!isDrawMode) {
            val oldScale = scaleFactor
            scaleFactor *= detector.scaleFactor
            // todo
            deltaScaleFactor = detector.scaleFactor

            mScrollX += detector.focusX * (oldScale - scaleFactor) / scaleFactor
            mScrollY += detector.focusY * (oldScale - scaleFactor) / scaleFactor

            focusPoint.set(measuredWidth / 2 * 1f, measuredHeight / 2 * 1f)
            matrixByImage.set(savedByImageMatrix)

            matrixByImage.postScale(
                detector.scaleFactor,
                detector.scaleFactor,
                focusPoint.x,
                focusPoint.y
            )

            // todo
            matrixByPoints.set(saveByPointsMatrix)
            matrixByPoints.setScale(scaleFactor, scaleFactor, focusPoint.x, focusPoint.y)

            getLastPointMatrixOffset()

            // todo
            isScale = true
            isScroll = false
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
            // todo
            matrixByPoints.set(saveByPointsMatrix)

            matrixByImage.postScale(backScale, backScale, measuredWidth / 2 * 1f, measuredHeight / 2 * 1f)
            // todo
            //  matrixByPoints.postScale(backScale, backScale, measuredWidth / 2 * 1f, measuredHeight / 2 * 1f)

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
            matrixByImage.set(savedByImageMatrix)
            matrixByImage.postTranslate(-dx, -dy)
            // todo
            matrixByPoints.set(saveByPointsMatrix)
            matrixByPoints.setTranslate(-dx, -dy)

            // matrixByPoints.setTranslate(-dx, -dy)
            checkBorders()
            setImageInVerticalCenter()
            getLastPointMatrixOffset()

            // todo
            mScrollX += -dx / 2
            mScrollY += -dy / 2

            isScale = false
            isScroll = true

            // todo
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

    fun setAllowZoomImage(isZoomImage: Boolean) {
        this.isZoomImage = isZoomImage
        getLastPointMatrixOffset()
        getDefaultMTRANSY()
    }

    private fun getLastPointMatrixOffset() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)

        differenceMixingMatrixX = abs(defaultMTRANSX - abs(values[Matrix.MTRANS_X]))
        differenceMixingMatrixY = abs(defaultMTRANSY - abs(values[Matrix.MTRANS_Y]))

        lastMTRANSX = abs(values[Matrix.MTRANS_X])
        lastMTRANSY = abs(values[Matrix.MTRANS_Y])
    }

    private fun getDefaultMTRANSY() {
        val values = FloatArray(9)
        matrixByImage.getValues(values)
        defaultMTRANSY = values[Matrix.MTRANS_Y]
        defaultMTRANSX = values[Matrix.MTRANS_X]
    }
}
