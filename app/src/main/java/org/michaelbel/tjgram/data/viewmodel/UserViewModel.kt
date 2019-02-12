package org.michaelbel.tjgram.data.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import org.michaelbel.tjgram.data.entity.User
import org.michaelbel.tjgram.data.persistence.LocalUser
import org.michaelbel.tjgram.data.persistence.UserDao

class UserViewModel(private val dataSource: UserDao) : ViewModel() {

    /*fun userName(): Flowable<String> {
        return dataSource.getUserById(USER_ID).map { user -> user.name }
    }*/

    fun localUser(userId: Int): Flowable<LocalUser> {
        return dataSource.getUserById(userId).map { user -> user }
    }

    fun updateUser(user: User): Completable {
        val localUser = LocalUser(
                id = user.id,
                name =  user.name,
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