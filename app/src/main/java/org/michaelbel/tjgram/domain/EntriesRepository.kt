package org.michaelbel.tjgram.domain

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.results.BooleanResult
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.api.results.EntryResult
import org.michaelbel.tjgram.data.api.results.LikesResult
import retrofit2.http.Field
import retrofit2.http.FieldMap
import java.util.*

interface EntriesRepository {
    fun subsiteTimeline(subsiteId: Long, sorting: String, count: Int, offset: Int): Observable<EntriesResult>
    fun entryComplaint(contentId: Int): Observable<BooleanResult>
    fun likeEntry(entryId: Int, sign: Int): Observable<LikesResult>
    fun entryCreate(title: String, text: String, subsiteId: Long, attaches: Map<String, String>): Observable<EntryResult>
}