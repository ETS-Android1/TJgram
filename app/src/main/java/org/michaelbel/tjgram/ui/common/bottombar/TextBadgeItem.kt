package org.michaelbel.tjgram.ui.common.bottombar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.widget.TextView

import org.michaelbel.tjgram.R

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

abstract class TextBadgeItem : BadgeItem<TextBadgeItem>() {

    private var mBackgroundColorResource: Int = 0
    private var mBackgroundColorCode: String? = null
    private var mBackgroundColor = Color.RED

    private var mTextColorResource: Int = 0
    private var mTextColorCode: String? = null
    private var mTextColor = Color.WHITE

    private var mText: CharSequence? = null

    private var mBorderColorResource: Int = 0
    private var mBorderColorCode: String? = null
    private var mBorderColor = Color.WHITE

    private var mBorderWidthInPixels = 0

    /**
     * @param colorResource resource for background color
     * @return this, to allow builder pattern
     */
    fun setBackgroundColorResource(@ColorRes colorResource: Int): TextBadgeItem {
        this.mBackgroundColorResource = colorResource
        refreshDrawable()
        return this
    }

    /**
     * @param colorCode color code for background color
     * @return this, to allow builder pattern
     */
    fun setBackgroundColor(colorCode: String?): TextBadgeItem {
        this.mBackgroundColorCode = colorCode
        refreshDrawable()
        return this
    }

    /**
     * @param color background color
     * @return this, to allow builder pattern
     */
    fun setBackgroundColor(color: Int): TextBadgeItem {
        this.mBackgroundColor = color
        refreshDrawable()
        return this
    }

    /**
     * @param colorResource resource for text color
     * @return this, to allow builder pattern
     */
    fun setTextColorResource(@ColorRes colorResource: Int): TextBadgeItem {
        this.mTextColorResource = colorResource
        setTextColor()
        return this
    }

    /**
     * @param colorCode color code for text color
     * @return this, to allow builder pattern
     */
    fun setTextColor(colorCode: String?): TextBadgeItem {
        this.mTextColorCode = colorCode
        setTextColor()
        return this
    }

    /**
     * @param color text color
     * @return this, to allow builder pattern
     */
    fun setTextColor(color: Int): TextBadgeItem {
        this.mTextColor = color
        setTextColor()
        return this
    }

    /**
     * @param text text to be set in badge (this shouldn't be empty text)
     * @return this, to allow builder pattern
     */
    fun setText(text: CharSequence?): TextBadgeItem {
        this.mText = text
        if (isWeakReferenceValid) {
            val textView = textView!!.get()
            if (!TextUtils.isEmpty(text)) {
                textView?.text = text
            }
        }
        return this
    }

    /**
     * @param colorResource resource for border color
     * @return this, to allow builder pattern
     */
    fun setBorderColorResource(@ColorRes colorResource: Int): TextBadgeItem {
        this.mBorderColorResource = colorResource
        refreshDrawable()
        return this
    }

    /**
     * @param colorCode color code for border color
     * @return this, to allow builder pattern
     */
    fun setBorderColor(colorCode: String?): TextBadgeItem {
        this.mBorderColorCode = colorCode
        refreshDrawable()
        return this
    }

    /**
     * @param color border color
     * @return this, to allow builder pattern
     */
    fun setBorderColor(color: Int): TextBadgeItem {
        this.mBorderColor = color
        refreshDrawable()
        return this
    }

    /**
     * @param borderWidthInPixels border width in pixels
     * @return this, to allow builder pattern
     */
    fun setBorderWidth(borderWidthInPixels: Int): TextBadgeItem {
        this.mBorderWidthInPixels = borderWidthInPixels
        refreshDrawable()
        return this
    }

    /**
     * @param context to fetch color
     * @return background color
     */
    private fun getBackgroundColor(context: Context): Int {
        return if (this.mBackgroundColorResource != 0) {
            ContextCompat.getColor(context, mBackgroundColorResource)
        } else if (!TextUtils.isEmpty(mBackgroundColorCode)) {
            Color.parseColor(mBackgroundColorCode)
        } else {
            mBackgroundColor
        }
    }

    /**
     * @param context to fetch color
     * @return text color
     */
    private fun getTextColor(context: Context): Int {
        return if (this.mTextColorResource != 0) {
            ContextCompat.getColor(context, mTextColorResource)
        } else if (!TextUtils.isEmpty(mTextColorCode)) {
            Color.parseColor(mTextColorCode)
        } else {
            mTextColor
        }
    }

    /**
     * @return text that needs to be set in badge
     */
    private fun getText(): CharSequence? {
        return mText
    }

    /**
     * @param context to fetch color
     * @return border color
     */
    private fun getBorderColor(context: Context): Int {
        return if (this.mBorderColorResource != 0) {
            ContextCompat.getColor(context, mBorderColorResource)
        } else if (!TextUtils.isEmpty(mBorderColorCode)) {
            Color.parseColor(mBorderColorCode)
        } else {
            mBorderColor
        }
    }

    /**
     * @return border width
     */
    private fun getBorderWidth(): Int {
        return mBorderWidthInPixels
    }

    /**
     * refresh's background drawable
     */
    private fun refreshDrawable() {
        if (isWeakReferenceValid) {
            val textView = textView!!.get()
            textView?.setBackgroundDrawable(getBadgeDrawable(textView.context))
        }
    }

    /**
     * set's new text color
     */
    private fun setTextColor() {
        if (isWeakReferenceValid) {
            val textView = textView!!.get()
            textView?.setTextColor(getTextColor(textView.context))
        }
    }

    /**
     * @param context to fetch color
     * @return return the background drawable
     */
    private fun getBadgeDrawable(context: Context): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = context.resources.getDimensionPixelSize(R.dimen.badge_corner_radius).toFloat()
        shape.setColor(getBackgroundColor(context))
        shape.setStroke(getBorderWidth(), getBorderColor(context))
        return shape
    }
}