package org.michaelbel.tjgram.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import org.michaelbel.tjgram.data.db.entities.UserData

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flowable<UserData>

    // todo test
    @Query("SELECT avatar_url FROM users WHERE id = :id")
    fun getUserAvatarById(id: Int): Flowable<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserData): Completable

    @Query("DELETE FROM users")
    fun deleteAllUsers()
}