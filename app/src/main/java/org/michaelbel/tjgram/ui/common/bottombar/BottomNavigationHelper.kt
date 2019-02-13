package org.michaelbel.tjgram.ui.common.bottombar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.ui.common.bottombar.utils.Utils
import org.michaelbel.tjgram.utils.DeviceUtil

object BottomNavigationHelper {

    /**
     * Used to get Measurements for MODE_FIXED
     *
     * @param context     to fetch measurements
     * @param screenWidth total screen width
     * @param noOfTabs    no of bottom bar tabs
     * @param scrollable  is bottom bar scrollable
     * @return width of each tab
     */
    fun getMeasurementsForFixedMode(context: Context, screenWidth: Int, noOfTabs: Int, scrollable: Boolean): IntArray {
        val result = IntArray(2)
        val minWidth = context.resources.getDimension(R.dimen.fixed_min_width_small_views).toInt()
        val maxWidth = context.resources.getDimension(R.dimen.fixed_min_width).toInt()
        var itemWidth = screenWidth / noOfTabs

        if (itemWidth < minWidth && scrollable) {
            itemWidth = context.resources.getDimension(R.dimen.fixed_min_width).toInt()
        } else if (itemWidth > maxWidth) {
            itemWidth = maxWidth
        }

        result[0] = itemWidth

        return result
    }

    /**
     * Used to get Measurements for MODE_SHIFTING
     *
     * @param context     to fetch measurements
     * @param screenWidth total screen width
     * @param noOfTabs    no of bottom bar tabs
     * @param scrollable  is bottom bar scrollable
     * @return min and max width of each tab
     */
    fun getMeasurementsForShiftingMode(context: Context, screenWidth: Int, noOfTabs: Int, scrollable: Boolean): IntArray {

        val result = IntArray(2)

        val minWidth = DeviceUtil.dp(context, 64f)
        val maxWidth = DeviceUtil.dp(context, 96f)

        val minPossibleWidth = minWidth * (noOfTabs + 0.5)
        val maxPossibleWidth = maxWidth * (noOfTabs + 0.75)
        var itemWidth: Int
        var itemActiveWidth: Int

        if (screenWidth < minPossibleWidth) {
            if (scrollable) {
                itemWidth = minWidth
                itemActiveWidth = (minWidth * 1.5).toInt()
            } else {
                itemWidth = (screenWidth / (noOfTabs + 0.5)).toInt()
                itemActiveWidth = (itemWidth * 1.5).toInt()
            }
        } else if (screenWidth > maxPossibleWidth) {
            itemWidth = maxWidth
            itemActiveWidth = (itemWidth * 1.75).toInt()
        } else {
            val minPossibleWidth1 = minWidth * (noOfTabs + 0.625)
            val minPossibleWidth2 = minWidth * (noOfTabs + 0.75)
            itemWidth = (screenWidth / (noOfTabs + 0.5)).toInt()
            itemActiveWidth = (itemWidth * 1.5).toInt()
            if (screenWidth > minPossibleWidth1) {
                itemWidth = (screenWidth / (noOfTabs + 0.625)).toInt()
                itemActiveWidth = (itemWidth * 1.625).toInt()
                if (screenWidth > minPossibleWidth2) {
                    itemWidth = (screenWidth / (noOfTabs + 0.75)).toInt()
                    itemActiveWidth = (itemWidth * 1.75).toInt()
                }
            }
        }

        result[0] = itemWidth
        result[1] = itemActiveWidth

        return result
    }

    /**
     * Used to get set data to the Tab views from navigation items
     *
     * @param bottomNavigationItem holds all the data
     * @param bottomNavigationTab  view to which data need to be set
     * @param bottomNavigationBar  view which holds all the tabs
     */
    fun bindTabWithData(bottomNavigationItem: BottomNavigationItem, bottomNavigationTab: BottomNavigationTab, bottomNavigationBar: BottomNavigationBar) {

        val context = bottomNavigationBar.context

        bottomNavigationTab.setLabel(bottomNavigationItem.getTitle(context))
        bottomNavigationTab.setIcon(bottomNavigationItem.getIcon(context))

        val activeColor = bottomNavigationItem.getActiveColor(context)
        val inActiveColor = bottomNavigationItem.getInActiveColor(context)

        if (activeColor != Utils.NO_COLOR) {
            bottomNavigationTab.activeColor = activeColor
        } else {
            bottomNavigationTab.activeColor = bottomNavigationBar.activeColor
        }

        if (inActiveColor != Utils.NO_COLOR) {
            bottomNavigationTab.setInactiveColor(inActiveColor)
        } else {
            bottomNavigationTab.setInactiveColor(bottomNavigationBar.inActiveColor)
        }

        if (bottomNavigationItem.isInActiveIconAvailable) {
            val inactiveDrawable = bottomNavigationItem.getInactiveIcon(context)
            if (inactiveDrawable != null) {
                bottomNavigationTab.setInactiveIcon(inactiveDrawable)
            }
        }

        bottomNavigationTab.setItemBackgroundColor(bottomNavigationBar.backgroundColor)

        val badgeItem = bottomNavigationItem.badgeItem
        badgeItem?.bindToBottomTab(bottomNavigationTab)
    }

    /**
     * Used to set the ripple animation when a tab is selected
     *
     * @param clickedView       the view that is clicked (to get dimens where ripple starts)
     * @param backgroundView    temporary view to which final background color is set
     * @param bgOverlay         temporary view which is animated to get ripple effect
     * @param newColor          the new color i.e ripple color
     * @param animationDuration duration for which animation runs
     */
    fun setBackgroundWithRipple(clickedView: View, backgroundView: View,
                                         bgOverlay: View, newColor: Int, animationDuration: Int) {
        val centerX = (clickedView.x + clickedView.measuredWidth / 2).toInt()
        val centerY = clickedView.measuredHeight / 2
        val finalRadius = backgroundView.width

        backgroundView.clearAnimation()
        bgOverlay.clearAnimation()

        val circularReveal: Animator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal = ViewAnimationUtils
                    .createCircularReveal(bgOverlay, centerX, centerY, 0f, finalRadius.toFloat())
        } else {
            bgOverlay.alpha = 0f
            circularReveal = ObjectAnimator.ofFloat(bgOverlay, "alpha", 0F, 1F)
        }

        circularReveal.duration = animationDuration.toLong()
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onCancel()
            }

            override fun onAnimationCancel(animation: Animator) {
                onCancel()
            }

            private fun onCancel() {
                backgroundView.setBackgroundColor(newColor)
                bgOverlay.visibility = View.GONE
            }
        })

        bgOverlay.setBackgroundColor(newColor)
        bgOverlay.visibility = View.VISIBLE
        circularReveal.start()
    }
}