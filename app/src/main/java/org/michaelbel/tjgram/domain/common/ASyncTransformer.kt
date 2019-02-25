package org.michaelbel.tjgram.domain.common

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ASyncTransformer<T> : Transformer<T>() {

    override fun apply(stream: Observable<T>): ObservableSource<T> {
        return stream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}