package org.michaelbel.tjgram.domain.usecases

import io.reactivex.Observable
import org.michaelbel.tjgram.domain.common.Transformer
import org.michaelbel.tjgram.domain.interactor.UseCase
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository

/**
 * Пожаловаться на контент.
 */
class SendReport(
        transformer: Transformer<Boolean>, private val repository: EntriesRemoteRepository
): UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_CONTENT_ID = "content_id"
    }

    fun reportEntry(contentId: Int): Observable<Boolean> {
        val data = HashMap<String, Any>()
        data[PARAM_CONTENT_ID] = contentId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val contentId = data?.get(PARAM_CONTENT_ID)
        contentId?.let {
            return repository.entryComplaint(it as Int).map { t-> t.result }
        } ?: return Observable.just(false)
    }
}