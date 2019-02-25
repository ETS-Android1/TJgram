package org.michaelbel.tjgram.data.repository

import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.api.results.UserResult
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.db.entities.UserData

class UsersRemoteRepository(private val service: TjApi, private val userDao: UserDao) {

    // Remote.
    fun getUserMe(): Observable<UserResult> =
            service.userMe().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    // Local.
    fun getUserById(id: Int): LiveData<UserData> = userDao.getUserById(id)

    // Local.
    fun getUserAvatar(id: Int): Observable<String> = userDao.getUserAvatarById(id)
}