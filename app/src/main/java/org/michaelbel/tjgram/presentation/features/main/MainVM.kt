package org.michaelbel.tjgram.presentation.features.main

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.presentation.base.BaseVM
import org.michaelbel.tjgram.presentation.base.Event

class MainVM(private val dataSource: UserDao): BaseVM() {

    private val _snackBarMessage = MutableLiveData<Event<Int>>()
    val snackBarMessage: LiveData<Event<Int>>
        get() = _snackBarMessage

    private val _toolbarTitle = MutableLiveData<Event<Int>>()
    val toolbarTitle: LiveData<Event<Int>>
        get() = _toolbarTitle

    /// test
    private val _userAvatar = MutableLiveData<String>()
    val userAvatar: LiveData<String>
        get() = _userAvatar

    fun showSnackBarMessage(@StringRes message: Int) {
        _snackBarMessage.value = Event(message)
    }

    fun changeToolbarTitle(@StringRes title: Int) {
        _toolbarTitle.value = Event(title)
    }

    fun changeUserAvatar(avatar: String) {
        _userAvatar.value = avatar
    }
}