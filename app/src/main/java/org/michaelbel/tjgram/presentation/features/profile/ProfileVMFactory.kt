package org.michaelbel.tjgram.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.dao.UserDao

@Suppress("unchecked_cast")
class ProfileVMFactory(
        private val service: TjApi, private val dataSource: UserDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileVM(service, dataSource) as T
    }
}