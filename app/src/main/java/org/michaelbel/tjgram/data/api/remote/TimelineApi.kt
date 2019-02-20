package org.michaelbel.tjgram.data.api.remote

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.entities.Entry
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("unused")
interface TimelineApi {

    @GET("timeline/{category}/{sorting}")
    fun timeline(
            @Path("category") category: String,
            @Path("sorting") sorting: String,
            @Query("count") count: Int,
            @Query("offset") offset: Int
    ): Observable<EntriesResult>

    @GET("timeline/mainpage")
    fun hashtag(
            @Query("hashtag") hashtag: String,
            @Query("count") count: Int,
            @Query("offset") offset: Int
    ): Observable<EntriesResult>

    @GET("news/default/recent")
    fun timelineNews(
            @Query("count") count: Int,
            @Query("offset") offset: Int
    ): Observable<EntriesResult>

    @GET("getflashholdedentry")
    fun flashholdedEntry(@Path("id") id: Int): Observable<Entry>
}