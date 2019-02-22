package org.michaelbel.tjgram.presentation.features.addpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.api.remote.TjApi

@Suppress("unchecked_cast")
class PostVMFactory(private val service: TjApi): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostVM(service) as T
    }
}