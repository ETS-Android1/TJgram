package org.michaelbel.tjgram.data.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.api.results.UserResult
import org.michaelbel.tjgram.domain.UsersRepository
import retrofit2.Response

class UsersRemoteRepository(private val service: TjApi): UsersRepository {

    override fun authQr(token: String): Observable<Response<UserResult>> {
        return service.authQr(token)
    }

    override fun userMe(): Observable<UserResult> {
        return service.userMe()
    }
}