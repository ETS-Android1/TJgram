package org.michaelbel.tjgram.presentation.features.main

import androidx.lifecycle.MutableLiveData
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.presentation.base.BaseVM

// fixme использовать Event для передачи событий между views
class MainVM(private val dataSource: UserDao): BaseVM() {

    val snackbarMessage = MutableLiveData<String>()

    fun showSnackbar(message: String) {
        snackbarMessage.value = message
    }

    val toolbarTitle = MutableLiveData<String>()

    fun setToolbarTitle(title: String) {
        toolbarTitle.value = title
    }

   /*

    // todo test
    fun userAvatar(userId: Int): Flowable<String> {
        return dataSource.getUserAvatarById(userId)
    }*/
}