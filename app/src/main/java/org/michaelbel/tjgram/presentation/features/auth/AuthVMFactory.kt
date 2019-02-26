package org.michaelbel.tjgram.presentation.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.domain.usecases.AuthQr

@Suppress("unchecked_cast")
class AuthVMFactory(private val authQr: AuthQr): ViewModelProvider.Factory {

    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        return AuthVM(authQr) as T
    }
}