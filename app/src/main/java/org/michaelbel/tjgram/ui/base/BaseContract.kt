package org.michaelbel.tjgram.ui.base

interface BaseContract {

    interface Presenter<V> {
        fun create(view: V)
        fun destroy()
    }
}