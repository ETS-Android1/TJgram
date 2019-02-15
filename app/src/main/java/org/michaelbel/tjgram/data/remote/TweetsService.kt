package org.michaelbel.tjgram.data.remote

import io.reactivex.Observable
import org.michaelbel.tjgram.data.entities.TweetsResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("unused")
interface TweetsService {

    @GET("tweets/{mode}")
    fun tweets(
        @Path("mode") mode: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Observable<TweetsResult>
}