package com.ashokvarma.bottomnavigation.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager

object Utils {

    const val NO_COLOR = Color.TRANSPARENT

    /**
     * @param context used to get system services
     * @return screenWidth in pixels
     */
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        wm.defaultDisplay.getSize(size)
        return size.x
    }

    /**
     * This method can be extended to get all android attributes color, string, dimension ...etc
     *
     * @param context          used to fetch android attribute
     * @param androidAttribute attribute codes like R.attr.colorAccent
     * @return in this case color of android attribute
     */
    fun fetchContextColor(context: Context, androidAttribute: Int): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(androidAttribute))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    /**
     * @param context used to fetch display metrics
     * @param dp      dp value
     * @return pixel value
     */
    fun dp2px(context: Context, dp: Float): Int {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
        return Math.round(px)
    }
}