package org.michaelbel.tjgram

interface BasePresenter<T> {
    var view: T
    fun onDestroy()
}