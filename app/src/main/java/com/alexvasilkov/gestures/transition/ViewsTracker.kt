package com.alexvasilkov.gestures.transition

import android.view.View

import com.alexvasilkov.gestures.transition.tracker.FromTracker
import com.alexvasilkov.gestures.transition.tracker.IntoTracker


@Deprecated("Use {@link GestureTransitions} class with {@link FromTracker} and\n" +
        "  {@link IntoTracker} instead.")
// Class is left for compatibility
interface ViewsTracker<ID> {

    /**
     * @param id Item ID
     * @return Position for item with given id, or [.NO_POSITION] if item was not found.
     */
    fun getPositionForId(id: ID): Int

    /**
     * @param position List position
     * @return Item's id at given position, or `null` if position is invalid.
     */
    fun getIdForPosition(position: Int): ID

    /**
     * @param position List position
     * @return View at given position, or `null` if there is no known view for given position
     * or position is invalid.
     */
    fun getViewForPosition(position: Int): View
}