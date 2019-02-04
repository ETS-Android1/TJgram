package org.michaelbel.tjgram.ui.profile

import io.reactivex.Observable
import org.michaelbel.tjgram.data.entity.User
import org.michaelbel.tjgram.data.entity.UserResult
import retrofit2.Response

interface ProfileContract {

    interface View {
        fun setUser(user: User, xToken: String)
        fun setError(throwable: Throwable)
    }

    interface Presenter {
        fun setView(view: View)
        fun getView() : View
        fun authQr(token: String)
        fun userMe()
        fun onDestroy()
    }

    interface Repository {
        fun authQr(token: String) : Observable<Response<UserResult>>
        fun userMe() : Observable<UserResult>
    }
}