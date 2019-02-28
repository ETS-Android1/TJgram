package org.michaelbel.tjgram.core.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import timber.log.Timber

object ViewUtil {

    fun clearCursorDrawable(editText: EditText) {
        val cursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        cursorDrawableRes.isAccessible = true
        cursorDrawableRes.setInt(editText, 0)
    }

    fun getIcon(context: Context, @DrawableRes resource: Int, colorFilter: Int): Drawable? =
            getIcon(context, resource, colorFilter, PorterDuff.Mode.MULTIPLY)

    fun getIcon(context: Context, @DrawableRes resource: Int, colorFilter: Int, mode: PorterDuff.Mode): Drawable? {
        val iconDrawable = ContextCompat.getDrawable(context, resource)
        val color = ContextCompat.getColor(context, colorFilter)

        if (iconDrawable != null) {
            iconDrawable.clearColorFilter()
            iconDrawable.mutate().setColorFilter(color, mode)
        }

        return iconDrawable
    }

    fun getAttrColor(context: Context, @AttrRes colorAttr: Int): Int {
        var color = 0
        val attrs = intArrayOf(colorAttr)

        try {
            val typedArray = context.obtainStyledAttributes(attrs)
            color = typedArray.getColor(0, 0)
            typedArray.recycle()
        } catch (e: Exception) {
            Timber.e(e)
        }

        return color
    }

    fun setScrollFlags(view: View, flags: Int): AppBarLayout.LayoutParams {
        val params = view.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = flags
        return params
    }
}