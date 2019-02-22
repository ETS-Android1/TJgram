package org.michaelbel.tjgram.presentation.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.db.entities.UserData
import org.michaelbel.tjgram.presentation.base.BaseVM

class ProfileVM(
        private val service: TjApi, private val dataSource: UserDao
): BaseVM() {

    private val _user = MutableLiveData<UserData>()
    val user: LiveData<UserData>
        get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getUser(id: Int) {
        disposable.add(dataSource.getUserById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    _user.value = it
                }, { throwable -> _error.value = throwable.message })
        )
    }

    /*fun userMe() {
        disposable.add(service.userMe().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResult ->
                    val user = userResult.result
                    _user.value = user
                }, { _error.value = it.message }))
    }*/
}