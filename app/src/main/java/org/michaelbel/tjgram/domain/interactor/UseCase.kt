package org.michaelbel.tjgram.domain.interactor

import io.reactivex.Observable
import org.michaelbel.tjgram.domain.common.Transformer

abstract class UseCase<T>(private val transformer: Transformer<T>) {

    abstract fun createObservable(data: Map<String, Any>? = null): Observable<T>

    fun observable(withData: Map<String, Any>? = null): Observable<T> {
        return createObservable(withData).compose(transformer)
    }
}