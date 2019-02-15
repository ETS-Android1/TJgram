package org.michaelbel.tjgram.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import org.michaelbel.tjgram.data.persistence.entities.LocalUser

@Dao
interface UserDao {

    @Query("SELECT * FROM Users WHERE id = :id")
    fun getUserById(id: Int): Flowable<LocalUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: LocalUser): Completable

    @Query("DELETE FROM Users")
    fun deleteAllUsers()
}