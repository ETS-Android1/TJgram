package org.michaelbel.tjgram.presentation.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.michaelbel.tjgram.data.entities.User
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import org.michaelbel.tjgram.presentation.base.BaseVM

class ProfileVM(private val repository: UsersRemoteRepository): BaseVM() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _userAvatar = MutableLiveData<String>()
    val userAvatar: LiveData<String>
        get() = _userAvatar

    fun getUser(id: Int) {
        disposable.add(repository.getUserMe().subscribe {
            _user.value = it.result
        })

        //val user: LiveData<UserData> = repository.getUserById(id)
        //_user.value = user.value

        //App.d("запрос данных юзера из бд: $id")

        /*disposable.add(repository.getUserById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    _user.value = it
                    App.d("Пользователь загружен: $it")
                }, { throwable ->
                    _error.value = throwable.message
                    App.d("get user error: ${throwable.message}")
                })
        )*/
    }

    /*fun getUserAvatar(id: Int) {
        Timber.d("Start load user $id avatar from room")
        repository.getUserAvatar(id)

        disposable.add(repository.getUserAvatar(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    _userAvatar.value = it
                    Timber.d("Аватарка загружена: $it")
                }, { throwable -> Timber.d("Ошибка при получении аватарки: ${throwable.message}") }))
    }*/

    /*fun userMe() {
        disposable.add(service.userMe().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResult ->
                    val user = userResult.result
                    _user.value = user
                }, { _error.value = it.message }))
    }*/
}