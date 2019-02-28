package org.michaelbel.tjgram.data.repository

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.api.results.BooleanResult
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.api.results.EntryResult
import org.michaelbel.tjgram.data.api.results.LikesResult
import org.michaelbel.tjgram.domain.EntriesRepository
import retrofit2.http.Field
import retrofit2.http.Path

class EntriesRemoteRepository(private val service: TjApi): EntriesRepository {

    override fun subsiteTimeline(subsiteId: Long, sorting: String, count: Int, offset: Int): Observable<EntriesResult> {
        return service.subsiteTimeline(subsiteId, sorting, count, offset)
    }

    override fun entryComplaint(contentId: Int): Observable<BooleanResult> {
        return service.entryComplaint(contentId)
    }

    override fun likeEntry(entryId: Int, sign: Int): Observable<LikesResult> {
        return service.likeEntry(entryId, sign)
    }

    override fun entryCreate(title: String, text: String, subsiteId: Long, attaches: Map<String, String>): Observable<EntryResult> {
        return service.entryCreate(title, text, subsiteId, attaches)
    }
}