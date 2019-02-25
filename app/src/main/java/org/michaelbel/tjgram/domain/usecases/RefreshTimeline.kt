package org.michaelbel.tjgram.domain.usecases

import io.reactivex.Observable
import org.michaelbel.tjgram.domain.common.Transformer
import org.michaelbel.tjgram.domain.interactor.UseCase
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository

/**
 * Получить список записей из указанного подсайта.
 */
class RefreshTimeline(
        transformer: Transformer<List<Entry>>, private val repository: EntriesRemoteRepository
): UseCase<List<Entry>>(transformer) {

    companion object {
        private const val PARAM_SUBSITE_ID = "subsite_id"
        private const val PARAM_SORTING = "sorting"
        private const val PARAM_COUNT = "count"
        private const val PARAM_OFFSET = "offset"
    }

    fun subsiteTimeline(subsiteId: Long, sorting: String, count: Int, offset: Int): Observable<List<Entry>> {
        val data = HashMap<String, Any>()
        data[PARAM_SUBSITE_ID] = subsiteId
        data[PARAM_SORTING] = sorting
        data[PARAM_COUNT] = count
        data[PARAM_OFFSET] = offset
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<Entry>> {
        val subsiteId = data?.get(PARAM_SUBSITE_ID)
        subsiteId?.let {
            return repository.subsiteTimeline(
                    data[PARAM_SUBSITE_ID] as Long,
                    data[PARAM_SORTING] as String,
                    data[PARAM_COUNT] as Int,
                    data[PARAM_OFFSET] as Int
            ).map { t-> t.results }
        } ?: return Observable.just(emptyList())
    }
}