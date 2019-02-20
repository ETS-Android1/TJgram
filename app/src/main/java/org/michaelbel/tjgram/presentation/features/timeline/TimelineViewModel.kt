package org.michaelbel.tjgram.presentation.features.timeline

import androidx.lifecycle.MutableLiveData
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.domain.common.Mapper
import org.michaelbel.tjgram.domain.usecases.GetEntries
import org.michaelbel.tjgram.presentation.common.BaseViewModel
import org.michaelbel.tjgram.presentation.common.SingleLiveEvent

class TimelineViewModel(private val getEntries: GetEntries/*, private val entryMapper: Mapper<Entry, Entry>*/) : BaseViewModel() {

    var viewState: MutableLiveData<TimelineViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<Throwable?> = SingleLiveEvent()

    init {
        viewState.value = TimelineViewState()
    }

    fun getEntries() {
        addDisposable(getEntries.observable()
            //.flatMap { entryMapper.observable(it) }
            .subscribe({ movies ->
                viewState.value?.let {
                    val newState = this.viewState.value?.copy(showLoading = false, entries = movies.results)
                    this.viewState.value = newState
                    this.errorState.value = null
                }

            }, {
                viewState.value = viewState.value?.copy(showLoading = false)
                errorState.value = it
            }))
    }
}