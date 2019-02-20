package org.michaelbel.tjgram.presentation.utils

import androidx.fragment.app.Fragment

object FragmentUtils {

    fun <T> Fragment.getListenerOrThrowException(listenerClazz: Class<T>): T {
        return getListener(listenerClazz) ?: throw IllegalStateException("Not found: " + listenerClazz.simpleName)
    }

    fun <T> Fragment.getListener(listenerClazz: Class<T>): T? {
        var listener = getListenerFromTargetFragment(listenerClazz)
        if (listener != null) {
            return listener
        }

        listener = getListenerFromParentFragment(listenerClazz)
        if (listener != null) {
            return listener
        }

        listener = getListenerFromActivity(listenerClazz)
        return listener
    }

    private fun <T> getListener(listenerClass: Class<T>, target: Any?): T? {
        return if (listenerClass.isInstance(target)) {
            listenerClass.cast(target)
        } else {
            null
        }
    }

    fun <T> Fragment.getListenerFromTargetFragment(listenerClazz: Class<T>): T? {
        return getListener(listenerClazz, targetFragment)
    }

    fun <T> Fragment.getListenerFromParentFragment(listenerClazz: Class<T>): T? {
        return getListener(listenerClazz, parentFragment)
    }

    fun <T> Fragment.getListenerFromActivity(listenerClazz: Class<T>): T? {
        return getListener(listenerClazz, activity)
    }
}