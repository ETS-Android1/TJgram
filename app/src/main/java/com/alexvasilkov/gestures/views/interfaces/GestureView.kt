package com.alexvasilkov.gestures.views.interfaces

import com.alexvasilkov.gestures.GestureController

/**
 * Common interface for all Gesture* views.
 *
 * All classes implementing this interface should be descendants of [android.view.View].
 */
interface GestureView {

    /**
     * Returns [GestureController] which is a main engine for all gestures interactions.
     *
     * Use it to apply settings, access and modify image state and so on.
     *
     * @return [GestureController].
     */
    val controller: GestureController
}