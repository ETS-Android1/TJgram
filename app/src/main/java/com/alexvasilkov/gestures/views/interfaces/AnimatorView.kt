package com.alexvasilkov.gestures.views.interfaces

import com.alexvasilkov.gestures.animation.ViewPositionAnimator

/**
 * Common interface for views supporting position animation.
 */
interface AnimatorView {

    /**
     * @return [ViewPositionAnimator] instance to control animation from other view position.
     */
    val positionAnimator: ViewPositionAnimator
}