package org.michaelbel.tjgram.presentation.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseVM: ViewModel() {

    protected var disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.clear()
    }
}