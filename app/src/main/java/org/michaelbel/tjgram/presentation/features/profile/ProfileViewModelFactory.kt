package org.michaelbel.tjgram.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("unchecked_cast")
class ProfileViewModelFactory(private val useCase: AuthUser): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(useCase) as T
    }
}