package org.michaelbel.tjgram.ui.common.bottombar

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import java.lang.ref.WeakReference

abstract class BadgeItem<T : BadgeItem<T>> {

    private var mGravity = Gravity.TOP or Gravity.END
    private var mHideOnSelect: Boolean = false

    /**
     * @return reference to text-view
     */
    var textView: WeakReference<BadgeTextView>? = null
        private set

    /**
     * @return if the badge is hidden
     */
    private var isHidden = false

    private var mAnimationDuration = 200

    /**
     * @return subClass to allow Builder pattern
     */
    internal abstract val subInstance: T

    ///////////////////////////////////////////////////////////////////////////
    // Internal Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return returns if BadgeTextView's reference is valid
     */
    val isWeakReferenceValid: Boolean
        get() = textView != null && textView!!.get() != null

    /**
     * if any extra binding is required binds all badgeItem, BottomNavigationTab and BadgeTextView
     *
     * @param bottomNavigationTab to which badgeItem needs to be attached
     */
    internal abstract fun bindToBottomTabInternal(bottomNavigationTab: BottomNavigationTab)

    ///////////////////////////////////////////////////////////////////////////
    // Public setter methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param gravity gravity of badge (TOP|LEFT ..etc)
     * @return this, to allow builder pattern
     */
    fun setGravity(gravity: Int): T {
        this.mGravity = gravity
        if (isWeakReferenceValid) {
            val textView = this.textView!!.get()
            val layoutParams = textView?.layoutParams as FrameLayout.LayoutParams
            layoutParams.gravity = gravity
            textView.layoutParams = layoutParams
        }
        return subInstance
    }

    /**
     * @param hideOnSelect if true hides badge on tab selection
     * @return this, to allow builder pattern
     */
    fun setHideOnSelect(hideOnSelect: Boolean): T {
        this.mHideOnSelect = hideOnSelect
        return subInstance
    }

    /**
     * @param animationDuration hide and show animation time
     * @return this, to allow builder pattern
     */
    fun setAnimationDuration(animationDuration: Int): T {
        this.mAnimationDuration = animationDuration
        return subInstance
    }


    ///////////////////////////////////////////////////////////////////////////
    // Library only access method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * binds all badgeItem, BottomNavigationTab and BadgeTextView
     *
     * @param bottomNavigationTab to which badgeItem needs to be attached
     */
    fun bindToBottomTab(bottomNavigationTab: BottomNavigationTab) {
        // set initial bindings
        bottomNavigationTab.badgeView.clearPrevious()
        if (bottomNavigationTab.badgeItem != null) {
            // removing old reference
            bottomNavigationTab.badgeItem.setTextView(null)
        }
        bottomNavigationTab.setBadgeItem(this)
        setTextView(bottomNavigationTab.badgeView)

        // allow sub class to modify the things
        bindToBottomTabInternal(bottomNavigationTab)

        // make view visible because gone by default
        bottomNavigationTab.badgeView.visibility = View.VISIBLE

        // set layout params based on gravity
        val layoutParams = bottomNavigationTab.badgeView.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = getGravity()
        bottomNavigationTab.badgeView.layoutParams = layoutParams

        // if hidden hide
        if (isHidden) {
            // if hide is called before the initialisation of bottom-bar this will handle that
            // by hiding it.
            hide()
        }
    }

    /**
     * Internal method used to update view when ever changes are made
     *
     * @param mTextView badge textView
     * @return this, to allow builder pattern
     */
    private fun setTextView(mTextView: BadgeTextView?): T {
        this.textView = WeakReference<BadgeTextView>(mTextView)
        return subInstance
    }

    /**
     * @return gravity of badge
     */
    private fun getGravity(): Int {
        return mGravity
    }

    /**
     * @return should hide on selection ?
     */
    fun isHideOnSelect(): Boolean {
        return mHideOnSelect
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal call back methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * callback from bottom navigation tab when it is selected
     */
    fun select() {
        if (mHideOnSelect) {
            hide(true)
        }
    }

    /**
     * callback from bottom navigation tab when it is un-selected
     */
    fun unSelect() {
        if (mHideOnSelect) {
            show(true)
        }
    }

    /**
     * @param animate whether to animate the change
     * @return this, to allow builder pattern
     */
    @JvmOverloads
    fun toggle(animate: Boolean = true): T {
        return if (isHidden) {
            show(animate)
        } else {
            hide(animate)
        }
    }

    /**
     * @param animate whether to animate the change
     * @return this, to allow builder pattern
     */
    @JvmOverloads
    fun show(animate: Boolean = true): T {
        isHidden = false
        if (isWeakReferenceValid) {
            val textView = this.textView?.get()!!
            if (animate) {
                textView.scaleX = 0f
                textView.scaleY = 0f
                textView.visibility = View.VISIBLE
                val animatorCompat = ViewCompat.animate(textView)
                animatorCompat.cancel()
                animatorCompat.duration = mAnimationDuration.toLong()
                animatorCompat.scaleX(1f).scaleY(1f)
                animatorCompat.setListener(null)
                animatorCompat.start()
            } else {
                textView.scaleX = 1f
                textView.scaleY = 1f
                textView.visibility = View.VISIBLE
            }
        }
        return subInstance
    }

    /**
     * @param animate whether to animate the change
     * @return this, to allow builder pattern
     */
    @JvmOverloads
    fun hide(animate: Boolean = true): T {
        isHidden = true
        if (isWeakReferenceValid) {
            val textView = this.textView!!.get()
            if (animate) {
                val animatorCompat = ViewCompat.animate(textView!!)
                animatorCompat.cancel()
                animatorCompat.duration = mAnimationDuration.toLong()
                animatorCompat.scaleX(0f).scaleY(0f)
                animatorCompat.setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {
                        // Empty body
                    }

                    override fun onAnimationEnd(view: View) {
                        view.visibility = View.GONE
                    }

                    override fun onAnimationCancel(view: View) {
                        view.visibility = View.GONE
                    }
                })
                animatorCompat.start()
            } else {
                textView?.visibility = View.GONE
            }
        }
        return subInstance
    }
}