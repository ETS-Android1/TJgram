package org.michaelbel.tjgram.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository

@Suppress("unchecked_cast")
class ProfileVMFactory(private val repository: UsersRemoteRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileVM(repository) as T
    }
}