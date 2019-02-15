package com.ashokvarma.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

import androidx.appcompat.widget.AppCompatTextView

@SuppressLint("Instantiatable")
class BadgeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var shapeBadgeItem: ShapeBadgeItem? = null

    private var areDimensOverridden: Boolean = false
    private var desiredWidth = 100
    private var desiredHeight = 100

    fun clearPrevious() {
        areDimensOverridden = false
        shapeBadgeItem = null
    }

    /**
     * @param shapeBadgeItem that can draw on top of the this view
     */
    fun setShapeBadgeItem(shapeBadgeItem: ShapeBadgeItem) {
        this.shapeBadgeItem = shapeBadgeItem
    }

    /**
     * if width and height of the view needs to be changed
     *
     * @param width new width that needs to be set
     * @param height new height that needs to be set
     */
    fun setDimens(width: Int, height: Int) {
        areDimensOverridden = true
        desiredWidth = width
        desiredHeight = height
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
        if (shapeBadgeItem != null) {
            shapeBadgeItem!!.draw(canvas)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (areDimensOverridden) {
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
                    Math.min(desiredWidth, widthSize)
                View.MeasureSpec.UNSPECIFIED ->
                    //Be whatever you want
                    desiredWidth
                else -> desiredWidth
            }

            //Measure Height
            height = when (heightMode) {
                View.MeasureSpec.EXACTLY ->
                    //Must be this size
                    heightSize
                View.MeasureSpec.AT_MOST ->
                    //Can't be bigger than...
                    Math.min(desiredHeight, heightSize)
                View.MeasureSpec.UNSPECIFIED ->
                    //Be whatever you want
                    desiredHeight
                else -> desiredHeight
            }

            //MUST CALL THIS
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
