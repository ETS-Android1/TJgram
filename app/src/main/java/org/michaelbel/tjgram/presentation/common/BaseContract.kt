package org.michaelbel.tjgram.presentation.common

interface BaseContract {

    interface View {
        fun showLoading(state: Boolean)
    }

    interface Presenter<V> {
        fun create(view: V)
        fun destroy()
    }
}