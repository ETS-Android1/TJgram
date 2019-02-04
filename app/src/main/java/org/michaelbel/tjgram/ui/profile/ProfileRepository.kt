package org.michaelbel.tjgram.ui.profile

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.entity.UserResult
import org.michaelbel.tjgram.data.remote.TjService
import retrofit2.Response

class ProfileRepository internal constructor(
    private val service: TjService) : ProfileContract.Repository {

    override fun authQr(token: String): Observable<Response<UserResult>> {
        return service.authQr(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun userMe(): Observable<UserResult> {
        return service.userMe().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}