package org.michaelbel.tjgram.ui.common.bottombar

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.utils.DeviceUtil

class ShiftingBottomNavigationTab : BottomNavigationTab {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    internal override fun init() {
        paddingTopActive = DeviceUtil.dp(context, 0f)
        paddingTopInActive = DeviceUtil.dp(context, 10f)

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.bnb_shifting_item, this, true)
        containerView = view.findViewById(R.id.shifting_bottom_navigation_container)
        labelView = view.findViewById<TextView>(R.id.shifting_bottom_navigation_title)
        iconView = view.findViewById<ImageView>(R.id.shifting_bottom_navigation_icon)
        iconContainerView = view.findViewById(R.id.shifting_bottom_navigation_icon_container)
        badgeView = view.findViewById(R.id.shifting_bottom_navigation_badge)

        super.init()
    }

    override fun select(setActiveColor: Boolean, animationDuration: Int) {
        super.select(setActiveColor, animationDuration)

        val anim = ResizeWidthAnimation(this, mActiveWidth)
        anim.duration = animationDuration.toLong()
        this.startAnimation(anim)

        labelView.animate().scaleY(1f).scaleX(1f).setDuration(animationDuration.toLong()).start()
    }

    override fun unSelect(setActiveColor: Boolean, animationDuration: Int) {
        super.unSelect(setActiveColor, animationDuration)

        val anim = ResizeWidthAnimation(this, mInActiveWidth)
        anim.duration = animationDuration.toLong()
        this.startAnimation(anim)

        labelView.animate().scaleY(0f).scaleX(0f).setDuration(0).start()
    }

    override fun setNoTitleIconContainerParams(layoutParams: FrameLayout.LayoutParams) {
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.shifting_no_title_icon_container_height)
        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.shifting_no_title_icon_container_width)
    }

    override fun setNoTitleIconParams(layoutParams: FrameLayout.LayoutParams) {
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.shifting_no_title_icon_height)
        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.shifting_no_title_icon_width)
    }

    private inner class ResizeWidthAnimation internal constructor(private val mView: View, private val mWidth: Int) : Animation() {
        private val mStartWidth: Int

        init {
            mStartWidth = mView.width
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            mView.layoutParams.width = mStartWidth + ((mWidth - mStartWidth) * interpolatedTime).toInt()
            mView.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
}