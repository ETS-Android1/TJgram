package org.michaelbel.tjgram.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.michaelbel.tjgram.data.db.dao.EntryDao
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.db.entities.UserData

@Database(entities = [UserData::class], version = 6, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun entryDao(): EntryDao
}