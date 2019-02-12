package org.michaelbel.tjgram.ui.common.bottombar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

import androidx.appcompat.widget.AppCompatTextView

@SuppressLint("Instantiatable")
class BadgeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mShapeBadgeItem: ShapeBadgeItem? = null

    private var mAreDimensOverridden: Boolean = false
    private var mDesiredWidth = 100
    private var mDesiredHeight = 100

    fun clearPrevious() {
        mAreDimensOverridden = false
        mShapeBadgeItem = null
    }

    /**
     * @param shapeBadgeItem that can draw on top of the this view
     */
    fun setShapeBadgeItem(shapeBadgeItem: ShapeBadgeItem) {
        mShapeBadgeItem = shapeBadgeItem
    }

    /**
     * if width and height of the view needs to be changed
     *
     * @param width new width that needs to be set
     * @param height new height that needs to be set
     */
    fun setDimens(width: Int, height: Int) {
        mAreDimensOverridden = true
        mDesiredWidth = width
        mDesiredHeight = height
        requestLayout()
    }

    /**
     * invalidate's view so badgeItem can draw again
     */
    fun recallOnDraw() {
        invalidate()
    }

    /**
     * {@inheritDoc}
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mShapeBadgeItem != null) {
            mShapeBadgeItem!!.draw(canvas)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mAreDimensOverridden) {
            val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

            val width: Int
            val height: Int

            //Measure Width
            width = when (widthMode) {
                View.MeasureSpec.EXACTLY ->
                    //Must be this size
                    widthSize
                View.MeasureSpec.AT_MOST ->
                    //Can't be bigger than...
                    Math.min(mDesiredWidth, widthSize)
                View.MeasureSpec.UNSPECIFIED ->
                    //Be whatever you want
                    mDesiredWidth
                else -> mDesiredWidth
            }

            //Measure Height
            height = when (heightMode) {
                View.MeasureSpec.EXACTLY ->
                    //Must be this size
                    heightSize
                View.MeasureSpec.AT_MOST ->
                    //Can't be bigger than...
                    Math.min(mDesiredHeight, heightSize)
                View.MeasureSpec.UNSPECIFIED ->
                    //Be whatever you want
                    mDesiredHeight
                else -> mDesiredHeight
            }

            //MUST CALL THIS
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
