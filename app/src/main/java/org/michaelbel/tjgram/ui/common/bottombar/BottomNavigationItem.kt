package org.michaelbel.tjgram.ui.common.bottombar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.michaelbel.tjgram.ui.common.bottombar.utils.Utils

/**
 * Class description : Holds data for tabs (i.e data structure which holds all data to paint a tab)
 *
 * @author ashokvarma
 * @version 1.0
 * @since 19 Mar 2016
 */
class BottomNavigationItem {

    private var mIconResource: Int = 0
    private var mIcon: Drawable? = null

    private var mInactiveIconResource: Int = 0
    private var mInactiveIcon: Drawable? = null
    /**
     * @return if in-active icon is set
     */
    internal var isInActiveIconAvailable = false
        private set

    private var mTitleResource: Int = 0
    private var mTitle: String? = null

    private var mActiveColorResource: Int = 0
    private var mActiveColorCode: String? = null
    private var mActiveColor: Int = 0

    private var mInActiveColorResource: Int = 0
    private var mInActiveColorCode: String? = null
    private var mInActiveColor: Int = 0

    /**
     * @return badge item that needs to set to respective view
     */
    internal var badgeItem: BadgeItem<*>? = null

    /**
     * @param mIconResource resource for the Tab icon.
     * @param mTitle        title for the Tab.
     */
    constructor(@DrawableRes mIconResource: Int, mTitle: String) {
        this.mIconResource = mIconResource
        this.mTitle = mTitle
    }

    /**
     * @param mIcon  drawable icon for the Tab.
     * @param mTitle title for the Tab.
     */
    constructor(mIcon: Drawable, mTitle: String) {
        this.mIcon = mIcon
        this.mTitle = mTitle
    }

    /**
     * @param mIcon          drawable icon for the Tab.
     * @param mTitleResource resource for the title.
     */
    constructor(mIcon: Drawable, @StringRes mTitleResource: Int) {
        this.mIcon = mIcon
        this.mTitleResource = mTitleResource
    }

    /**
     * @param mIconResource  resource for the Tab icon.
     * @param mTitleResource resource for the title.
     */
    constructor(@DrawableRes mIconResource: Int, @StringRes mTitleResource: Int) {
        this.mIconResource = mIconResource
        this.mTitleResource = mTitleResource
    }

    /**
     * By default library will switch the color of icon provided (in between active and in-active icons)
     * This method is used, if people need to set different icons for active and in-active modes.
     *
     * @param mInactiveIcon in-active drawable icon
     * @return this, to allow builder pattern
     */
    fun setInactiveIcon(mInactiveIcon: Drawable?): BottomNavigationItem {
        if (mInactiveIcon != null) {
            this.mInactiveIcon = mInactiveIcon
            isInActiveIconAvailable = true
        }
        return this
    }

    /**
     * By default library will switch the color of icon provided (in between active and in-active icons)
     * This method is used, if people need to set different icons for active and in-active modes.
     *
     * @param mInactiveIconResource resource for the in-active icon.
     * @return this, to allow builder pattern
     */
    fun setInactiveIconResource(@DrawableRes mInactiveIconResource: Int): BottomNavigationItem {
        this.mInactiveIconResource = mInactiveIconResource
        isInActiveIconAvailable = true
        return this
    }


    /**
     * @param colorResource resource for active color
     * @return this, to allow builder pattern
     */
    fun setActiveColorResource(@ColorRes colorResource: Int): BottomNavigationItem {
        this.mActiveColorResource = colorResource
        return this
    }

    /**
     * @param colorCode color code for active color
     * @return this, to allow builder pattern
     */
    fun setActiveColor(colorCode: String?): BottomNavigationItem {
        this.mActiveColorCode = colorCode
        return this
    }

    /**
     * @param color active color
     * @return this, to allow builder pattern
     */
    fun setActiveColor(color: Int): BottomNavigationItem {
        this.mActiveColor = color
        return this
    }

    /**
     * @param colorResource resource for in-active color
     * @return this, to allow builder pattern
     */
    fun setInActiveColorResource(@ColorRes colorResource: Int): BottomNavigationItem {
        this.mInActiveColorResource = colorResource
        return this
    }

    /**
     * @param colorCode color code for in-active color
     * @return this, to allow builder pattern
     */
    fun setInActiveColor(colorCode: String?): BottomNavigationItem {
        this.mInActiveColorCode = colorCode
        return this
    }

    /**
     * @param color in-active color
     * @return this, to allow builder pattern
     */
    fun setInActiveColor(color: Int): BottomNavigationItem {
        this.mInActiveColor = color
        return this
    }

    /**
     * @param badgeItem badge that needs to be displayed for this tab
     * @return this, to allow builder pattern
     */
    fun setBadgeItem(badgeItem: ShapeBadgeItem?): BottomNavigationItem {
        this.badgeItem = badgeItem
        return this
    }

    /**
     * @param badgeItem badge that needs to be displayed for this tab
     * @return this, to allow builder pattern
     */
    fun setBadgeItem(badgeItem: TextBadgeItem?): BottomNavigationItem {
        this.badgeItem = badgeItem
        return this
    }

    ///////////////////////////////////////////////////////////////////////////
    // Library only access method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param context to fetch drawable
     * @return icon drawable
     */
    internal fun getIcon(context: Context): Drawable? {
        return if (this.mIconResource != 0) {
            ContextCompat.getDrawable(context, this.mIconResource)
        } else {
            this.mIcon
        }
    }

    /**
     * @param context to fetch resource
     * @return title string
     */
    internal fun getTitle(context: Context): String? {
        return if (this.mTitleResource != 0) {
            context.getString(this.mTitleResource)
        } else {
            this.mTitle
        }
    }

    /**
     * @param context to fetch resources
     * @return in-active icon drawable
     */
    internal fun getInactiveIcon(context: Context): Drawable? {
        return if (this.mInactiveIconResource != 0) {
            ContextCompat.getDrawable(context, this.mInactiveIconResource)
        } else {
            this.mInactiveIcon
        }
    }

    /**
     * @param context to fetch color
     * @return active color (or) -1 if no color is specified
     */
    internal fun getActiveColor(context: Context): Int {
        return if (this.mActiveColorResource != 0) {
            ContextCompat.getColor(context, mActiveColorResource)
        } else if (!TextUtils.isEmpty(mActiveColorCode)) {
            Color.parseColor(mActiveColorCode)
        } else if (this.mActiveColor != 0) {
            mActiveColor
        } else {
            Utils.NO_COLOR
        }
    }

    /**
     * @param context to fetch color
     * @return in-active color (or) -1 if no color is specified
     */
    internal fun getInActiveColor(context: Context): Int {
        return if (this.mInActiveColorResource != 0) {
            ContextCompat.getColor(context, mInActiveColorResource)
        } else if (!TextUtils.isEmpty(mInActiveColorCode)) {
            Color.parseColor(mInActiveColorCode)
        } else if (this.mInActiveColor != 0) {
            mInActiveColor
        } else {
            Utils.NO_COLOR
        }
    }
}