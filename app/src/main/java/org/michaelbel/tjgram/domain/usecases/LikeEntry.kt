package org.michaelbel.tjgram.domain.usecases

import io.reactivex.Observable
import org.michaelbel.tjgram.domain.common.Transformer
import org.michaelbel.tjgram.domain.interactor.UseCase
import org.michaelbel.tjgram.data.entities.LikesForResult
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository

/**
 * Оценить запись
 */
class LikeEntry(
        transformer: Transformer<LikesForResult>, private val repository: EntriesRemoteRepository
): UseCase<LikesForResult>(transformer) {

    companion object {
        private const val PARAM_ENTRY_ID = "entry_id"
        private const val PARAM_SIGN = "sign"
    }

    fun likeEntry(entryId: Int, sign: Int): Observable<LikesForResult> {
        val data = HashMap<String, Any>()
        data[PARAM_ENTRY_ID] = entryId
        data[PARAM_SIGN] = sign
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<LikesForResult> {
        val entryId = data?.get(PARAM_ENTRY_ID)
        entryId?.let {
            return repository.likeEntry(
                    data[PARAM_ENTRY_ID] as Int,
                    data[PARAM_SIGN] as Int
            ).map { t-> t.result }
        } ?: return Observable.just(null)
    }
}