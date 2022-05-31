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
added recalculation on scrollimport androidx.core.graphics.drawable.toBitmap
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

    // todo
    var allPoints = HashMap<MyPath, Paint>()
    private var mLastPaths = HashMap<MyPath, Paint>()
    private var mUndonePaths = HashMap<MyPath, Paint>()

    private var mPath = MyPath()
    private var mTransform = Matrix()

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
            savedMatrix.set(matrixByImage)
            scaleGestureDetector.onTouchEvent(event)
            if (!isScaling && scaleFactor > 1) {
                gestureDetector.onTouchEvent(event)
                // todo
               // if (handleScroll(event)) return true
            }
            imageMatrix = matrixByImage

            val points2: FloatArray = floatArrayOf(event.x, event.y)
            mTransform.mapPoints(points2)
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
                            //  mUndonePaths.clear()
                        }
                    }
                    // Движение пальца пользователя по экрану
                    MotionEvent.ACTION_MOVE -> if (isEventToMatrix(event.x, event.y) && modeTouchBehavior)
                        actionMove(x, y)
                    // срабатывает при отпускании последнего пальца
                    MotionEvent.ACTION_UP -> if (isEventToMatrix(event.x, event.y)) actionUp()
                }
            }

//            if (isDrawMode) {
//                when (event.action and MotionEvent.ACTION_MASK) {
//                    MotionEvent.ACTION_CANCEL,
//                        // срабатывает при отпускании последнего пальца
//                    MotionEvent.ACTION_UP,
//                        // срабатывает при касании первого пальца
//                    MotionEvent.ACTION_DOWN -> {
//                        val isPoint = isEventToMatrix(event.x, event.y)
//                        if (isPoint) {
//                            val startPoint = PointF(event.x, event.y)
//                            points.add(startPoint)
//                            modeTouchBehavior = true
//                        }
//                    }
//                    // срабатывает при касании каждого последующего пальца к примеру второй
//                    MotionEvent.ACTION_POINTER_DOWN,
//                        // Движение пальца пользователя по экрану
//                    MotionEvent.ACTION_MOVE -> {
//                        val isPoint = isEventToMatrix(event.x, event.y)
//                        if (!isPoint) {
//                            points.clear()
//                            val point = PointF(event.x, event.y)
//                            points.add(point)
//                        }
//
//                        if (modeTouchBehavior && isPoint) {
//                            stopFocusPoint.set(event.x, event.y)
//                            val latestPoint = points.lastOrNull()
//                            val point = PointF(event.x, event.y)
//                            points.add(point)
//                            latestPoint?.let { startFocusPoint.set(latestPoint.x, latestPoint.y) }
//                        }
//                    }
//                    // срабатывает при отпускании каждого пальца кроме последнего
//                    MotionEvent.ACTION_POINTER_UP -> {
//                        modeTouchBehavior = false
//                    }
//                }
//            }
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
        mTransform.setTranslate(mScrollX + canvas.clipBounds.centerX(), mScrollY + canvas.clipBounds.centerY())
        mTransform.postScale(scaleFactor, scaleFactor)

        val bg = mBackground
        if (bg != null) {
            mBackgroundRect.left = -bg.width.toFloat() / 2
            mBackgroundRect.right = bg.width.toFloat() / 2
            mBackgroundRect.top = -bg.height.toFloat() / 2
            mBackgroundRect.bottom = bg.height.toFloat() / 2
            if (bg.height == 1 && bg.width == 1)
                mBackgroundRect.set(canvas.clipBounds)
            else
                mTransform.mapRect(mBackgroundRect)

            canvas.drawBitmap(drawable.toBitmap(), null, mBackgroundRect, null)
        }
        canvas.withMatrix(mTransform) {
            mTransform.invert(mTransform)

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
            mScrollX += detector.focusX * (oldScale - scaleFactor) / scaleFactor
            mScrollY += detector.focusY * (oldScale - scaleFactor) / scaleFactor

            focusPoint.set(detector.focusX, detector.focusY)
            matrixByImage.set(savedMatrix)
            matrixByImage.postScale(
                detector.scaleFactor,
                detector.scaleFactor,
                focusPoint.x,
                focusPoint.y
            )
            getLastPointMatrixOffset()
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
            // mTransform.postTranslate(-dx,-dy)
            checkBorders()
            setImageInVerticalCenter()
            getLastPointMatrixOffset()

            mScrollX += -dx / 2
            mScrollY += -dy / 2

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
