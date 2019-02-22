package org.michaelbel.tjgram.presentation.features.profile.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.views.DeviceUtil

class QrFindView: RelativeLayout {

    companion object {
        private const val ANIMATION_DELAY = 100L
    }

    private val paint = Paint()
    private val maskColor = ContextCompat.getColor(context, R.color.transparent50)
    private val frameColor = ContextCompat.getColor(context, R.color.foreground)
    private var frameRect: Rect? = null
    private val focusThick = 2

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        if (isInEditMode) {
            return
        }

        setWillNotDraw(false)
        val inflater = LayoutInflater.from(context)

        val parentLayout = inflater.inflate(R.layout.layout_qr_scanner, this) as RelativeLayout
        val frameLayout = parentLayout.findViewById<FrameLayout>(R.id.qrCodeScanner)

        frameRect = Rect()
        val layoutParams = frameLayout.layoutParams as RelativeLayout.LayoutParams
        frameRect!!.left = (DeviceUtil.getScreenWidth(context) - layoutParams.width) / 2
        frameRect!!.top = layoutParams.topMargin
        frameRect!!.right = frameRect!!.left + layoutParams.width
        frameRect!!.bottom = frameRect!!.top + layoutParams.height
    }

    public override fun onDraw(canvas: Canvas) {
        if (isInEditMode) {
            return
        }

        val frame = frameRect ?: return

        val width = width
        val height = height

        paint.color = maskColor
        canvas.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), paint)
        canvas.drawRect(0f, frame.top.toFloat(), frame.left.toFloat(), (frame.bottom + 1).toFloat(), paint)
        canvas.drawRect((frame.right + 1).toFloat(), frame.top.toFloat(), width.toFloat(), (frame.bottom + 1).toFloat(), paint)
        canvas.drawRect(0f, (frame.bottom + 1).toFloat(), width.toFloat(), height.toFloat(), paint)

        drawFocusRect(canvas, frame)
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom)
    }

    private fun drawFocusRect(canvas: Canvas, rect: Rect) {
        paint.color = frameColor
        canvas.drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), (rect.top + focusThick).toFloat(), paint)
        canvas.drawRect(rect.left.toFloat(), rect.top.toFloat(), (rect.left + focusThick).toFloat(), rect.bottom.toFloat(), paint)
        canvas.drawRect((rect.right - focusThick).toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
        canvas.drawRect(rect.left.toFloat(), (rect.bottom - focusThick).toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
    }
}