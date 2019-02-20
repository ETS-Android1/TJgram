package org.michaelbel.tjgram.presentation.features.profile

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.results.UserResult
import org.michaelbel.tjgram.domain.UserRepository
import org.michaelbel.tjgram.domain.common.Transformer
import org.michaelbel.tjgram.domain.usecases.UseCase
import retrofit2.Response

class AuthUser(transformer: Transformer<Response<UserResult>>, private val userRepository: UserRepository): UseCase<Response<UserResult>>(transformer) {

    private var token: String? = null

    fun setToken(token: String) {
        this.token = token
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Response<UserResult>> {
        return userRepository.authQr(token!!)
    }
}