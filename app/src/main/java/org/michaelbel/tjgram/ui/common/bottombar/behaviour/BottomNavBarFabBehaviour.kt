package org.michaelbel.tjgram.ui.common.bottombar.behaviour

import android.view.View
import android.view.animation.Interpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.michaelbel.tjgram.ui.common.bottombar.BottomNavigationBar

class BottomNavBarFabBehaviour : CoordinatorLayout.Behavior<FloatingActionButton>() {

    private var mFabTranslationYAnimator: ViewPropertyAnimatorCompat? = null

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        return isDependent(dependency) || super.layoutDependsOn(parent, child, dependency)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: FloatingActionButton, layoutDirection: Int): Boolean {
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection)
        updateFabTranslationForBottomNavigationBar(parent, child, null)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        if (isDependent(dependency)) {
            updateFabTranslationForBottomNavigationBar(parent, child, dependency)
            return false
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View) {
        if (isDependent(dependency)) {
            updateFabTranslationForBottomNavigationBar(parent, child, dependency)
        }
    }

    private fun isDependent(dependency: View): Boolean {
        return dependency is BottomNavigationBar || dependency is Snackbar.SnackbarLayout
    }

    private fun updateFabTranslationForBottomNavigationBar(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View?) {
        val snackBarTranslation = getFabTranslationYForSnackBar(parent, fab)
        val bottomBarParameters = getFabTranslationYForBottomNavigationBar(parent, fab)
        val bottomBarTranslation = bottomBarParameters[0]
        val bottomBarHeight = bottomBarParameters[1]

        val targetTransY: Float
        targetTransY = if (snackBarTranslation >= bottomBarTranslation) {
            // when snackBar is below BottomBar in translation present.
            bottomBarTranslation
        } else {
            snackBarTranslation
        }

        val currentTransY = fab.translationY

        // Make sure that any current animation is cancelled
        ensureOrCancelAnimator(fab)

        if (fab.isShown && Math.abs(currentTransY - targetTransY) > fab.height * 0.667f) {
            // If the FAB will be travelling by more than 2/3 of it's height, let's animate it instead
            mFabTranslationYAnimator!!.translationY(targetTransY).start()
        } else {
            // Now update the translation Y
            fab.translationY = targetTransY
        }
    }

    private fun getFabTranslationYForBottomNavigationBar(parent: CoordinatorLayout, fab: FloatingActionButton): FloatArray {
        var minOffset = 0f
        var viewHeight = 0f
        val dependencies = parent.getDependencies(fab)
        var i = 0
        val z = dependencies.size
        while (i < z) {
            val view = dependencies[i]
            if (view is BottomNavigationBar) {
                viewHeight = view.getHeight().toFloat()
                minOffset = Math.min(minOffset, view.getTranslationY() - viewHeight)
            }
            i++
        }

        return floatArrayOf(minOffset, viewHeight)
    }

    private fun getFabTranslationYForSnackBar(parent: CoordinatorLayout, fab: FloatingActionButton): Float {
        var minOffset = 0f
        val dependencies = parent.getDependencies(fab)
        var i = 0
        val z = dependencies.size
        while (i < z) {
            val view = dependencies[i]
            if (view is Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset, view.getTranslationY() - view.getHeight())
            }
            i++
        }

        return minOffset
    }

    private fun ensureOrCancelAnimator(fab: FloatingActionButton) {
        if (mFabTranslationYAnimator == null) {
            mFabTranslationYAnimator = ViewCompat.animate(fab)
            mFabTranslationYAnimator!!.duration = 400
            mFabTranslationYAnimator!!.interpolator = FAST_OUT_SLOW_IN_INTERPOLATOR
        } else {
            mFabTranslationYAnimator!!.cancel()
        }
    }

    companion object {
        internal val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()
    }
}