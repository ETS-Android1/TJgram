package org.michaelbel.tjgram.ui.profile.view

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
import org.michaelbel.tjgram.utils.DeviceUtil

class QrFinderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mPaint: Paint
    private val mMaskColor: Int
    private val mFrameColor: Int
    private var mFrameRect: Rect? = null
    private val mFocusThick: Int

    init {
        mPaint = Paint()
        mMaskColor = ContextCompat.getColor(context, R.color.transparent50)
        mFrameColor = ContextCompat.getColor(context, R.color.foreground)
        mFocusThick = 2
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

        mFrameRect = Rect()
        val layoutParams = frameLayout.layoutParams as RelativeLayout.LayoutParams
        mFrameRect!!.left = (DeviceUtil.getScreenWidth(context) - layoutParams.width) / 2
        mFrameRect!!.top = layoutParams.topMargin
        mFrameRect!!.right = mFrameRect!!.left + layoutParams.width
        mFrameRect!!.bottom = mFrameRect!!.top + layoutParams.height
    }

    public override fun onDraw(canvas: Canvas) {
        if (isInEditMode) {
            return
        }

        val frame = mFrameRect ?: return

        val width = width
        val height = height

        mPaint.color = mMaskColor
        canvas.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), mPaint)
        canvas.drawRect(0f, frame.top.toFloat(), frame.left.toFloat(), (frame.bottom + 1).toFloat(), mPaint)
        canvas.drawRect((frame.right + 1).toFloat(), frame.top.toFloat(), width.toFloat(), (frame.bottom + 1).toFloat(), mPaint)
        canvas.drawRect(0f, (frame.bottom + 1).toFloat(), width.toFloat(), height.toFloat(), mPaint)

        drawFocusRect(canvas, frame)
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom)
    }

    private fun drawFocusRect(canvas: Canvas, rect: Rect) {
        mPaint.color = mFrameColor
        canvas.drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), (rect.top + mFocusThick).toFloat(), mPaint)
        canvas.drawRect(rect.left.toFloat(), rect.top.toFloat(), (rect.left + mFocusThick).toFloat(), rect.bottom.toFloat(), mPaint)
        canvas.drawRect((rect.right - mFocusThick).toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), mPaint)
        canvas.drawRect(rect.left.toFloat(), (rect.bottom - mFocusThick).toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), mPaint)
    }

    companion object {

        private val ANIMATION_DELAY = 100L
    }
}