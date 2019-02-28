package org.michaelbel.tjgram.domain.usecases

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.results.UserResult
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import org.michaelbel.tjgram.domain.common.Transformer
import org.michaelbel.tjgram.domain.interactor.UseCase
import retrofit2.Response

/**
 * Опубликовать новую запись.
 */
class CreatePost(
        transformer: Transformer<Entry>, private val repository: EntriesRemoteRepository
): UseCase<Entry>(transformer) {

    companion object {
        private const val PARAM_TITLE = "title"
        private const val PARAM_INTRO = "text"
        private const val PARAM_SUBSITE_ID = "subsite_id"
        private const val PARAM_ATTACHES = "attaches"
    }

    fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>): Observable<Entry> {
        val data = HashMap<String, Any>()
        data[PARAM_TITLE] = title
        data[PARAM_INTRO] = text
        data[PARAM_SUBSITE_ID] = subsiteId
        data[PARAM_ATTACHES] = attaches
        return observable(data)
    }

    @Suppress("unchecked_cast")
    override fun createObservable(data: Map<String, Any>?): Observable<Entry> {
        val title = data?.get(PARAM_TITLE)
        title?.let {
            return repository.entryCreate(
                    data[PARAM_TITLE] as String,
                    data[PARAM_INTRO] as String,
                    data[PARAM_SUBSITE_ID] as Long,
                    data[PARAM_ATTACHES] as Map<String, String>
            ).map { t-> t.result }
        } ?: return Observable.just(null)
    }
}