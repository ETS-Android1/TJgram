package org.michaelbel.tjgram.modules.profile

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.remote.TjService
import timber.log.Timber

class ProfilePresenter(private val service: TjService) : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null
    private val disposables = CompositeDisposable()

    override fun create(view: ProfileContract.View) {
        this.view = view
    }

    override fun authQr(token: String) {
        disposables.add(service.authQr(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
            if (response.isSuccessful) {
                val headers = response.headers()
                val xDeviceToken = headers.get("X-Device-Token")

                val userResult = response.body()
                if (userResult != null) {
                    val user = userResult.result
                    view!!.setUserMe(user!!, xDeviceToken)
                }
            }
        }, { throwable -> view!!.setAuthError(throwable) }))
    }

    override fun userMe() {
        disposables.add(service.userMe().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ userResult ->
            val user = userResult.result
            view!!.setUserMe(user!!, "x")
        },  { Timber.e(it) }))
    }

    override fun destroy() {
        disposables.dispose()
    }
}