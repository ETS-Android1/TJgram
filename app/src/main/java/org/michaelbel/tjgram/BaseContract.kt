package org.michaelbel.tjgram

interface BaseContract {

    interface Presenter<V> {
        fun create(view: V)
        fun destroy()
    }
}