package org.michaelbel.tjgram.presentation.utils.recycler

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView

open class LinearSmoothScrollerMiddle(context: Context) : RecyclerView.SmoothScroller() {

    companion object {
        private const val MILLISECONDS_PER_INCH = 25f
        private const val TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f
        private const val TARGET_SEEK_SCROLL_DISTANCE_PX = 10000
    }

    private val mLinearInterpolator = LinearInterpolator()
    private val mDecelerateInterpolator = DecelerateInterpolator(1.5f)

    private val millisecondsPerPx: Float
    private var mTargetVector: PointF? = null

    private var mInterimTargetDx = 0
    private var mInterimTargetDy = 0

    init {
        millisecondsPerPx = MILLISECONDS_PER_INCH / context.resources.displayMetrics.densityDpi
    }

    override fun onStart() {}

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: RecyclerView.SmoothScroller.Action) {
        val dy = calculateDyToMakeVisible(targetView)
        val time = calculateTimeForDeceleration(dy)
        if (time > 0) {
            action.update(0, -dy, Math.max(400, time), mDecelerateInterpolator)
        }
    }

    override fun onSeekTargetStep(dx: Int, dy: Int, state: RecyclerView.State, action: RecyclerView.SmoothScroller.Action) {
        if (childCount == 0) {
            stop()
            return
        }

        mInterimTargetDx = clampApplyScroll(mInterimTargetDx, dx)
        mInterimTargetDy = clampApplyScroll(mInterimTargetDy, dy)

        if (mInterimTargetDx == 0 && mInterimTargetDy == 0) {
            updateActionForInterimTarget(action)
        }
    }

    override fun onStop() {
        mInterimTargetDy = 0
        mInterimTargetDx = mInterimTargetDy
        mTargetVector = null
    }

    private fun calculateTimeForDeceleration(dx: Int): Int {
        return Math.ceil(calculateTimeForScrolling(dx) / .3356).toInt()
    }

    private fun calculateTimeForScrolling(dx: Int): Int {
        return Math.ceil((Math.abs(dx) * millisecondsPerPx).toDouble()).toInt()
    }

    private fun updateActionForInterimTarget(action: RecyclerView.SmoothScroller.Action) {
        val scrollVector = computeScrollVectorForPosition(targetPosition)

        if (scrollVector == null || scrollVector.x == 0f && scrollVector.y == 0f) {
            val target = targetPosition
            action.jumpTo(target)
            stop()
            return
        }

        normalize(scrollVector)
        mTargetVector = scrollVector

        mInterimTargetDx = (TARGET_SEEK_SCROLL_DISTANCE_PX * scrollVector.x).toInt()
        mInterimTargetDy = (TARGET_SEEK_SCROLL_DISTANCE_PX * scrollVector.y).toInt()

        val time = calculateTimeForScrolling(TARGET_SEEK_SCROLL_DISTANCE_PX)
        action.update((mInterimTargetDx * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(), (mInterimTargetDy *
                TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(), (time * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(), mLinearInterpolator)
    }

    private fun clampApplyScroll(tmpdt: Int, dt: Int): Int {
        var tmpDt = tmpdt
        val before = tmpDt

        tmpDt -= dt

        return if (before * tmpDt <= 0) {
            0
        } else tmpDt
    }

    private fun calculateDyToMakeVisible(view: View): Int {
        val layoutManager = layoutManager

        if (layoutManager == null || !layoutManager.canScrollVertically()) {
            return 0
        }

        val params = view.layoutParams as RecyclerView.LayoutParams
        val top = layoutManager.getDecoratedTop(view) - params.topMargin
        val bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin
        var start = layoutManager.paddingTop
        var end = layoutManager.height - layoutManager.paddingBottom

        val boxSize = end - start
        val viewSize = bottom - top

        start = (boxSize - viewSize) / 2
        end = start + viewSize

        val dtStart = start - top

        if (dtStart > 0) {
            return dtStart
        }

        val dtEnd = end - bottom

        return if (dtEnd < 0) {
            dtEnd
        } else 0
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        val layoutManager = layoutManager

        return if (layoutManager is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            (layoutManager as RecyclerView.SmoothScroller.ScrollVectorProvider).computeScrollVectorForPosition(targetPosition)
        } else null
    }
}