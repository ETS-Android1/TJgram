package org.michaelbel.tjgram.presentation.features.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.data.entities.LikesForResult
import org.michaelbel.tjgram.presentation.base.BaseVM
import org.michaelbel.tjgram.domain.usecases.LikeEntry
import org.michaelbel.tjgram.domain.usecases.RefreshTimeline
import org.michaelbel.tjgram.domain.usecases.SendReport

class TimelineVM(
        private val refreshTimeline: RefreshTimeline,
        private val sendReport: SendReport,
        private val likeEntry: LikeEntry
): BaseVM() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _reportSent = MutableLiveData<Boolean>()
    val reportSent: LiveData<Boolean>
        get() = _reportSent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _dataLoadingError = MutableLiveData<String>()
    val dataLoadingError: LiveData<String>
        get() = _dataLoadingError

    private val _items = MutableLiveData<List<Entry>>().apply { value = emptyList() }
    val items: LiveData<List<Entry>>
        get() = _items

    private val _refreshedItems = MutableLiveData<List<Entry>>().apply { value = emptyList() }
    val refreshedItems: LiveData<List<Entry>>
        get() = _refreshedItems

    private val _likedEntry = MutableLiveData<LikesForResult>()
    val likedEntry: LiveData<LikesForResult>
        get() = _likedEntry

    private val _likedEntryError = MutableLiveData<Throwable>()
    val likedEntryError: LiveData<Throwable>
        get() = _likedEntryError

    fun complaintEntry(contentId: Int) {
        disposable.add(sendReport.reportEntry(contentId)
                .subscribe ({ _reportSent.value = it }, { _reportSent.value = false })
        )
    }

    fun entries(subsiteId: Long, sorting: String, offset: Int) {
        disposable.add(refreshTimeline.subsiteTimeline(subsiteId, sorting, PAGE_SIZE, offset)
                .doOnSubscribe { _dataLoading.value = true }
                .doAfterTerminate { _dataLoading.value = false }
                .subscribe({ _items.value = it }, { throwable -> _dataLoadingError.value = throwable.message })
        )
    }

    fun entriesNext(subsiteId: Long, sorting: String, offset: Int) {
        /**
         * Подгрузка записей по мере прокрутки списка.
         * Не нужно отображать progressBar и показывать emptyView при ошибке загрузки.
         */
        disposable.add(refreshTimeline.subsiteTimeline(subsiteId, sorting, PAGE_SIZE, offset)
                .subscribe { _items.value = it }
        )
    }

    fun entriesRefresh(subsiteId: Long, sorting: String, offset: Int) {
        disposable.add(refreshTimeline.subsiteTimeline(subsiteId, sorting, PAGE_SIZE, offset)
                .doOnSubscribe { _dataLoading.value = true }
                .doAfterTerminate { _dataLoading.value = false }
                .subscribe({ _refreshedItems.value = it }, { throwable -> _dataLoadingError.value = throwable.message })
        )
    }

    fun likeEntry(entry: Entry, sign: Int) {
        disposable.add(likeEntry.likeEntry(entry.id, sign)
                .subscribe ({ _likedEntry.value = it }, { _likedEntryError.value = it })
        )
    }
}