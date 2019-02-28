package org.michaelbel.tjgram.domain.usecases

import io.reactivex.Observable
import org.michaelbel.tjgram.data.api.results.UserResult
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import org.michaelbel.tjgram.domain.common.Transformer
import org.michaelbel.tjgram.domain.interactor.UseCase
import retrofit2.Response

/**
 * Авторизоваться с помощью QR-кода
 */
class AuthQr(
        transformer: Transformer<Response<UserResult>>, private val repository: UsersRemoteRepository
): UseCase<Response<UserResult>>(transformer) {

    companion object {
        private const val PARAM_TOKEN = "token"
    }

    fun authQr(token: String): Observable<Response<UserResult>> {
        val data = HashMap<String, Any>()
        data[PARAM_TOKEN] = token
        return observable(data)
    }

    // todo с помощью map возвращать только User.
    override fun createObservable(data: Map<String, Any>?): Observable<Response<UserResult>> {
        val token = data?.get(PARAM_TOKEN)
        token?.let {
            return repository.authQr(it as String)
        } ?: return Observable.just(null)
    }
}