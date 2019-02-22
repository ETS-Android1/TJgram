package org.michaelbel.tjgram.presentation.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.api.remote.TjApi

@Suppress("unchecked_cast")
class TimelineVMFactory(private val service: TjApi): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineVM(service) as T
    }
}