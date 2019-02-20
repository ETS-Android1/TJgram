package org.michaelbel.tjgram.presentation.features.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.db.entities.UserData
import org.michaelbel.tjgram.data.entities.User

class MainViewModel(private val dataSource: UserDao): ViewModel() {

    private val disposables = CompositeDisposable()

    // Show Snackbar on MainActivity.
    val snackbarMessage = MutableLiveData<String>()

    fun showSnackbar(message: String) {
        snackbarMessage.value = message
    }

    // Change Toolbar Title.
    val toolbarTitle = MutableLiveData<String>()

    fun setToolbarTitle(title: String) {
        toolbarTitle.value = title
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun localUser(userId: Int): Flowable<UserData> {
        return dataSource.getUserById(userId).map { user -> user }
    }

    // todo test
    fun userAvatar(userId: Int): Flowable<String> {
        return dataSource.getUserAvatarById(userId)
    }

    fun updateUser(user: User): Completable {
        val localUser = UserData(
                id = user.id,
                name = user.name,
                avatarUrl = user.avatarUrl,
                createdDate = user.createdDate,
                createdDateRFC = user.createdDateRFC,
                karma = user.karma,
                pushTopic = user.pushTopic,
                url = user.url,
                userHash = user.userHash,

                tjSubscriptionActive = user.advancedAccess.tjSubscription.isActive
        )
        return dataSource.insertUser(localUser)
    }
}