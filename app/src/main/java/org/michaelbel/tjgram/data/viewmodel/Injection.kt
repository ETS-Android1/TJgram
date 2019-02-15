package org.michaelbel.tjgram.data.viewmodel

import android.content.Context
import org.michaelbel.tjgram.data.persistence.AppDatabase
import org.michaelbel.tjgram.data.persistence.dao.UserDao

object Injection {

    private fun provideUserDataSource(context: Context): UserDao {
        val database = AppDatabase.getInstance(context)
        return database.userDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return ViewModelFactory(dataSource)
    }
}