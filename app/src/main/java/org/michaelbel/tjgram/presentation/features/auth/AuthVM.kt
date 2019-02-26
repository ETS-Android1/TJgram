package org.michaelbel.tjgram.presentation.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.db.entities.UserData
import org.michaelbel.tjgram.data.entities.User
import org.michaelbel.tjgram.domain.usecases.AuthQr
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.base.BaseVM

class AuthVM(private val authQr: AuthQr): BaseVM() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun authQr(token: String) {
        disposable.add(authQr.authQr(token)
                .doOnSubscribe {}
                .doAfterTerminate {}
                .subscribe ({
                    val xDeviceToken = it.headers().get("X-Device-Token")
                    _token.value = xDeviceToken

                    val userResult = it.body()
                    if (userResult != null) {
                        val user = userResult.result
                        if (user != null) {
                            _user.value = user
                        }
                    }
                }, { throwable -> _error.value = throwable.message })
        )
    }

    /*private fun updateUser(user: User): Completable {
        val localUser = UserData(
                id = user.id,
                name = user.name,
                karma = user.karma,
                createdDate = user.createdDate,
                createdDateRFC = user.createdDateRFC,
                avatarUrl = user.avatarUrl,
                pushTopic = user.pushTopic,
                url = user.url,
                userHash = user.userHash,

                advancedAccessHash = user.advancedAccess.hash,
                needAdvancedAccess = user.advancedAccess.isNeedsAdvancedAccess,

                readComments = user.advancedAccess.actions.isReadComments,
                writeComments = user.advancedAccess.actions.isWriteComments,

                tjSubscriptionActive = user.advancedAccess.tjSubscription.isActive,
                tjSubscriptionActiveUntil = user.advancedAccess.tjSubscription.activeUntil
        )
        return dataSource.insertUser(localUser)
    }*/
}