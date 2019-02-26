package org.michaelbel.tjgram.domain

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.results.BooleanResult
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.api.results.LikesResult
import org.michaelbel.tjgram.data.api.results.UserResult
import retrofit2.Response
import java.util.*

interface UsersRepository {
    fun authQr(token: String): Observable<Response<UserResult>>
    fun userMe(): Observable<UserResult>
}