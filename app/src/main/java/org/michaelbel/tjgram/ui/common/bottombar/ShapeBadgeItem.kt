package org.michaelbel.tjgram.ui.common.bottombar

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import org.michaelbel.tjgram.ui.common.bottombar.utils.Utils
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class ShapeBadgeItem : BadgeItem<ShapeBadgeItem>() {

    @Shape
    private var mShape = SHAPE_STAR_5_VERTICES

    private var mShapeColorCode: String? = null
    private var mShapeColorResource: Int = 0
    private var mShapeColor = Color.RED

    // init values set at bindToBottomTabInternal
    private var mHeightInPixels: Int = 0
    private var mWidthInPixels: Int = 0
    private var mEdgeMarginInPx: Int = 0

    private val mCanvasRect = RectF()
    private val mCanvasPaint: Paint
    private val mPath = Path()// used for pathDrawables

    /**
     * {@inheritDoc}
     */
    internal override val subInstance: ShapeBadgeItem
        get() = this

    @IntDef(SHAPE_OVAL, SHAPE_RECTANGLE, SHAPE_HEART, SHAPE_STAR_3_VERTICES, SHAPE_STAR_4_VERTICES, SHAPE_STAR_5_VERTICES, SHAPE_STAR_6_VERTICES)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class Shape

    init {
        mCanvasPaint = Paint()
        mCanvasPaint.color = mShapeColor
        // If stroke needed
        //            paint.setStrokeWidth(widthInPx);
        //            paint.setStyle(Paint.Style.STROKE);
        mCanvasPaint.isAntiAlias = true
        mCanvasPaint.style = Paint.Style.FILL
    }

    ///////////////////////////////////////////////////////////////////////////
    // public methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param shape new shape that needs to be drawn
     * @return this, to allow builder pattern
     */
    fun setShape(@Shape shape: Int): ShapeBadgeItem {
        this.mShape = shape
        refreshDraw()
        return this
    }

    /**
     * @param colorResource resource for background color
     * @return this, to allow builder pattern
     */
    fun setShapeColorResource(@ColorRes colorResource: Int): ShapeBadgeItem {
        this.mShapeColorResource = colorResource
        refreshColor()
        return this
    }

    /**
     * @param colorCode color code for background color
     * @return this, to allow builder pattern
     */
    fun setShapeColor(colorCode: String?): ShapeBadgeItem {
        this.mShapeColorCode = colorCode
        refreshColor()
        return this
    }

    /**
     * @param color background color
     * @return this, to allow builder pattern
     */
    fun setShapeColor(color: Int): ShapeBadgeItem {
        this.mShapeColor = color
        refreshColor()
        return this
    }

    /**
     * @param context    to convert dp to pixel
     * @param heightInDp dp size for height of badge item
     * @param widthInDp  dp size for width of badge item
     * @return this, to allow builder pattern
     */
    fun setSizeInDp(context: Context, heightInDp: Int, widthInDp: Int): ShapeBadgeItem {
        mHeightInPixels = Utils.dp2px(context, heightInDp.toFloat())
        mWidthInPixels = Utils.dp2px(context, widthInDp.toFloat())
        if (isWeakReferenceValid) {
            textView!!.get()!!.setDimens(mWidthInPixels, mHeightInPixels)
        }
        return this
    }

    /**
     * @param heightInPx pixel size for height of badge item
     * @param widthInPx  pixel size for width of badge item
     * @return this, to allow builder pattern
     */
    fun setSizeInPixels(heightInPx: Int, widthInPx: Int): ShapeBadgeItem {
        mHeightInPixels = heightInPx
        mWidthInPixels = widthInPx
        if (isWeakReferenceValid) {
            textView!!.get()?.setDimens(mWidthInPixels, mHeightInPixels)
        }
        return this
    }

    /**
     * @param context        to convert dp to pixel
     * @param edgeMarginInDp dp size for margin of badge item
     * @return this, to allow builder pattern
     */
    fun setEdgeMarginInDp(context: Context, edgeMarginInDp: Int): ShapeBadgeItem {
        mEdgeMarginInPx = Utils.dp2px(context, edgeMarginInDp.toFloat())
        refreshMargin()
        return this
    }

    /**
     * @param edgeMarginInPx pixel size for margin of badge item
     * @return this, to allow builder pattern
     */
    fun setEdgeMarginInPixels(edgeMarginInPx: Int): ShapeBadgeItem {
        mEdgeMarginInPx = edgeMarginInPx
        refreshMargin()
        return this
    }

    ///////////////////////////////////////////////////////////////////////////
    // Library internal methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * draw's specified shape
     *
     * @param canvas on which shape has to be drawn
     */
    internal fun draw(canvas: Canvas) {
        mCanvasRect.set(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat())
        when (mShape) {
            SHAPE_RECTANGLE -> canvas.drawRect(mCanvasRect, mCanvasPaint)
            SHAPE_OVAL -> canvas.drawOval(mCanvasRect, mCanvasPaint)
            SHAPE_STAR_3_VERTICES, SHAPE_STAR_4_VERTICES, SHAPE_STAR_5_VERTICES, SHAPE_STAR_6_VERTICES -> drawStar(canvas, mShape)
            SHAPE_HEART -> drawHeart(canvas)
        }
    }

    /**
     * {@inheritDoc}
     */
    internal override fun bindToBottomTabInternal(bottomNavigationTab: BottomNavigationTab) {
        if (mHeightInPixels == 0)
            mHeightInPixels = Utils.dp2px(bottomNavigationTab.context, 12f)
        if (mWidthInPixels == 0)
            mWidthInPixels = Utils.dp2px(bottomNavigationTab.context, 12f)
        if (mEdgeMarginInPx == 0)
            mEdgeMarginInPx = Utils.dp2px(bottomNavigationTab.context, 4f)

        refreshMargin()
        refreshColor()// so that user set color will be updated

        bottomNavigationTab.badgeView.setShapeBadgeItem(this)

        bottomNavigationTab.badgeView.setDimens(mWidthInPixels, mHeightInPixels)
    }


    ///////////////////////////////////////////////////////////////////////////
    // Class only access methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return shape color
     */
    private fun getShapeColor(context: Context): Int {
        return if (this.mShapeColorResource != 0) {
            ContextCompat.getColor(context, mShapeColorResource)
        } else if (!TextUtils.isEmpty(mShapeColorCode)) {
            Color.parseColor(mShapeColorCode)
        } else {
            mShapeColor
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Internal Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * refresh's paint color if set and redraw's shape with new color
     */
    private fun refreshColor() {
        if (isWeakReferenceValid) {
            mCanvasPaint.color = getShapeColor(textView!!.get()?.getContext()!!)
        }
        refreshDraw()
    }

    /**
     * notifies BadgeTextView to invalidate so it will draw again and redraws shape
     */
    private fun refreshDraw() {
        if (isWeakReferenceValid) {
            textView!!.get()?.recallOnDraw()
        }
    }

    /**
     * refresh's margin if set
     */
    private fun refreshMargin() {
        if (isWeakReferenceValid) {
            val layoutParams = textView!!.get()?.getLayoutParams() as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = mEdgeMarginInPx
            layoutParams.topMargin = mEdgeMarginInPx
            layoutParams.rightMargin = mEdgeMarginInPx
            layoutParams.leftMargin = mEdgeMarginInPx
            textView!!.get()?.setLayoutParams(layoutParams)
        }
    }

    /**
     * @param canvas  on which star needs to be drawn
     * @param numOfPt no of points a star should have
     */
    private fun drawStar(canvas: Canvas, numOfPt: Int) {
        val section = 2.0 * Math.PI / numOfPt
        val halfSection = section / 2.0
        val antiClockRotation = getStarAntiClockRotationOffset(numOfPt)

        val x = canvas.width.toFloat() / 2.0f
        val y = canvas.height.toFloat() / 2.0f
        val radius: Float
        val innerRadius: Float

        if (canvas.width > canvas.height) {
            radius = canvas.height * 0.5f
            innerRadius = canvas.height * 0.25f
        } else {
            radius = canvas.width * 0.5f
            innerRadius = canvas.width * 0.25f
        }

        mPath.reset()

        mPath.moveTo(
                (x + radius * Math.cos(0 - antiClockRotation)).toFloat(),
                (y + radius * Math.sin(0 - antiClockRotation)).toFloat())
        mPath.lineTo(
                (x + innerRadius * Math.cos(0 + halfSection - antiClockRotation)).toFloat(),
                (y + innerRadius * Math.sin(0 + halfSection - antiClockRotation)).toFloat())

        for (i in 1 until numOfPt) {
            mPath.lineTo(
                    (x + radius * Math.cos(section * i - antiClockRotation)).toFloat(),
                    (y + radius * Math.sin(section * i - antiClockRotation)).toFloat())
            mPath.lineTo(
                    (x + innerRadius * Math.cos(section * i + halfSection - antiClockRotation)).toFloat(),
                    (y + innerRadius * Math.sin(section * i + halfSection - antiClockRotation)).toFloat())
        }

        mPath.close()

        canvas.drawPath(mPath, mCanvasPaint)
    }

    /**
     * offset to make star shape look straight
     *
     * @param numOfPt no of points a star should have
     */
    private fun getStarAntiClockRotationOffset(numOfPt: Int): Double {
        if (numOfPt == 5) {
            return 2.0 * Math.PI / 20.0 // quarter of (section angle for 5 point star) = 36 degrees
        } else if (numOfPt == 6) {
            return 2.0 * Math.PI / 12.0 // half the (section angle for 6 point star) = 60 degrees
        }
        return 0.0
    }

    private fun drawHeart(canvas: Canvas) {
        val curveLength = (canvas.height / 3).toFloat()

        mPath.reset()
        mPath.moveTo((canvas.width / 2).toFloat(), canvas.height.toFloat())// bottom part
        mPath.lineTo(curveLength / 3, 7 * curveLength / 4)
        mPath.arcTo(RectF(0f, 0f, (canvas.width / 2).toFloat(), 2 * curveLength), -225f, 225f)
        mPath.arcTo(RectF((canvas.width / 2).toFloat(), 0f, canvas.width.toFloat(), 2 * curveLength), -180f, 225f)
        mPath.close()

        canvas.drawPath(mPath, mCanvasPaint)
    }

    companion object {

        const val SHAPE_OVAL = 0
        const val SHAPE_RECTANGLE = 1
        const val SHAPE_HEART = 2
        const val SHAPE_STAR_3_VERTICES = 3
        const val SHAPE_STAR_4_VERTICES = 4
        const val SHAPE_STAR_5_VERTICES = 5
        const val SHAPE_STAR_6_VERTICES = 6
    }
}