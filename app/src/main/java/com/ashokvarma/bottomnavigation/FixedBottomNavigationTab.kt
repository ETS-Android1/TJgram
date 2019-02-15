package com.ashokvarma.bottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.michaelbel.tjgram.R

class FixedBottomNavigationTab : BottomNavigationTab {

    private var labelScale: Float = 0.toFloat()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    internal override fun init() {
        paddingTopActive = resources.getDimension(R.dimen.fixed_height_top_padding_active).toInt()
        paddingTopInActive = resources.getDimension(R.dimen.fixed_height_top_padding_inactive).toInt()

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.bnb_item, this, true)
        containerView = view.findViewById(R.id.fixed_bottom_navigation_container)
        labelView = view.findViewById(R.id.fixed_bottom_navigation_title)
        iconView = view.findViewById(R.id.fixed_bottom_navigation_icon)
        iconContainerView = view.findViewById(R.id.fixed_bottom_navigation_icon_container)
        badgeView = view.findViewById(R.id.fixed_bottom_navigation_badge)

        userAvatar = view.findViewById(R.id.user_avatar)

        labelScale = resources.getDimension(R.dimen.fixed_label_inactive) / resources.getDimension(R.dimen.fixed_label_active)
        super.init()
    }

    override fun select(setActiveColor: Boolean, animationDuration: Int) {
        labelView.animate().scaleX(1f).scaleY(1f).setDuration(animationDuration.toLong()).start()
        super.select(setActiveColor, animationDuration)
    }

    override fun unSelect(setActiveColor: Boolean, animationDuration: Int) {
        labelView.animate().scaleX(labelScale).scaleY(labelScale).setDuration(animationDuration.toLong()).start()
        super.unSelect(setActiveColor, animationDuration)
    }

    override fun setNoTitleIconContainerParams(layoutParams: FrameLayout.LayoutParams) {
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.fixed_no_title_icon_container_height)
        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.fixed_no_title_icon_container_width)
    }

    override fun setNoTitleIconParams(layoutParams: FrameLayout.LayoutParams) {
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.fixed_no_title_icon_height)
        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.fixed_no_title_icon_width)
    }
}