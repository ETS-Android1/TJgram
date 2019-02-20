package org.michaelbel.tjgram.presentation.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel: ViewModel() {

    private val disposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) {
        this.disposable.add(disposable)
    }

    override fun onCleared() {
        disposable.clear()
    }
}