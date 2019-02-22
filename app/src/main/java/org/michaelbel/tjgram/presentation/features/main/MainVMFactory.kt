package org.michaelbel.tjgram.presentation.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.db.dao.UserDao

@Suppress("unchecked_cast")
class MainVMFactory(private val dataSource: UserDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainVM(dataSource) as T
    }
}