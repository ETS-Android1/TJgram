package org.michaelbel.tjgram.ui.newphoto.view

import android.content.Context
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatImageView

class SquaredImage : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}