package org.michaelbel.tjgram.presentation.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.domain.common.Mapper
import org.michaelbel.tjgram.domain.usecases.GetEntries

class TimelineViewModelFactory(private val useCase: GetEntries/*, private val mapper: Mapper<Entry, Entry>*/) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(useCase/*, mapper*/) as T
    }
}