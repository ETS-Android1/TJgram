package org.michaelbel.tjgram.ui.common.bottombar.behaviour

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.snackbar.Snackbar
import org.michaelbel.tjgram.ui.common.bottombar.BottomNavigationBar
import java.lang.ref.WeakReference

class BottomVerticalScrollBehavior<V: View> : VerticalScrollingBehavior<V>() {

    private var bottomNavHeight: Int = 0
    private var viewRef: WeakReference<BottomNavigationBar>? = null

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection)
        if (child is BottomNavigationBar) {
            viewRef = WeakReference(child as BottomNavigationBar)
        }

        child.post { bottomNavHeight = child.height }
        updateSnackBarPosition(parent, child, getSnackBarInstance(parent, child))
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return isDependent(dependency) || super.layoutDependsOn(parent, child, dependency)
    }

    private fun isDependent(dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isDependent(dependency)) {
            updateSnackBarPosition(parent, child, dependency)
            return false
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

    private fun updateSnackBarPosition(parent: CoordinatorLayout, child: V, dependency: View?, translationY: Float = child.translationY - child.height) {
        if (dependency is Snackbar.SnackbarLayout) {
            ViewCompat.animate(dependency).setInterpolator(INTERPOLATOR).setDuration(80).setStartDelay(0).translationY(translationY).start()
        }
    }

    private fun getSnackBarInstance(parent: CoordinatorLayout, child: V): Snackbar.SnackbarLayout? {
        val dependencies = parent.getDependencies(child)
        var i = 0
        val z = dependencies.size
        while (i < z) {
            val view = dependencies[i]
            if (view is Snackbar.SnackbarLayout) {
                return view
            }
            i++
        }
        return null
    }

    override fun onNestedVerticalScrollUnconsumed(coordinatorLayout: CoordinatorLayout, child: V, @ScrollDirection scrollDirection: Int, currentOverScroll: Int, totalScroll: Int) {}

    override fun onNestedVerticalPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, @ScrollDirection scrollDirection: Int) {
        //handleDirection(child, scrollDirection);
    }

    override fun onNestedDirectionFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float, consumed: Boolean, @ScrollDirection scrollDirection: Int): Boolean {
        /*if (consumed) {
            handleDirection(child, scrollDirection);
        }*/
        return consumed
    }

    override fun onNestedVerticalScrollConsumed(coordinatorLayout: CoordinatorLayout, child: V, @ScrollDirection scrollDirection: Int, currentOverScroll: Int, totalConsumedScroll: Int) {
        handleDirection(coordinatorLayout, child, scrollDirection)
    }

    private fun handleDirection(parent: CoordinatorLayout, child: V, scrollDirection: Int) {
        val bottomNavigationBar = viewRef!!.get()
        if (bottomNavigationBar != null && bottomNavigationBar.isAutoHideEnabled) {
            if (scrollDirection == VerticalScrollingBehavior.ScrollDirection.SCROLL_DIRECTION_DOWN && bottomNavigationBar.isHidden) {
                updateSnackBarPosition(parent, child, getSnackBarInstance(parent, child), (-bottomNavHeight).toFloat())
                bottomNavigationBar.show()
            } else if (scrollDirection == VerticalScrollingBehavior.ScrollDirection.SCROLL_DIRECTION_UP && !bottomNavigationBar.isHidden) {
                updateSnackBarPosition(parent, child, getSnackBarInstance(parent, child), 0f)
                bottomNavigationBar.hide()
            }
        }
    }

    companion object {
        private val INTERPOLATOR = FastOutSlowInInterpolator()
    }
}