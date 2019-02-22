package org.michaelbel.tjgram.presentation.features.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.api.results.LikesResult
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.presentation.base.BaseVM
import java.util.*

class TimelineVM(private val service: TjApi): BaseVM() {

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

    private val _likeEntry = MutableLiveData<LikesResult>()
    val likeEntry: LiveData<LikesResult>
        get() = _likeEntry

    fun complaintEntry(contentId: Int) {
        disposable.add(service.entryComplaint(contentId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ baseResult ->
                    val status = baseResult.result
                    if (status != null) {
                        _reportSent.value = true
                    }
                }, { _reportSent.value = false }))
    }

    fun entries(subsiteId: Long, sorting: String, offset: Int) {
        disposable.add(service.timeline(EntriesResult.Category.MAINPAGE, EntriesResult.Sorting.RECENT, PAGE_SIZE, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _dataLoading.value = true }
                .doAfterTerminate { _dataLoading.value = false }
                .subscribe({ (results) ->
                    val result = ArrayList(results)
                    _items.value = result
                }, {
                    throwable -> _dataLoadingError.value = throwable.message
                })
        )
    }

    fun entriesNext(subsiteId: Long, sorting: String, offset: Int) {
        disposable.add(service.timeline(EntriesResult.Category.MAINPAGE, EntriesResult.Sorting.RECENT, PAGE_SIZE, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                /*.retry(5L)*/
                .subscribe({ (results) ->
                    val result = ArrayList(results)
                    _items.value = result
                }, { throwable ->
                    _dataLoadingError.value = throwable.message
                    // fixme список уже частично загружен, отображать load more view у recyclerView с возможностью подгрузить данные
                })
        )
    }

    /*fun likeEntry(entry: Entry, sign: Int) {
        disposable.add(service.likeEntry(entry.id, sign).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { likesResult -> _likeEntry.value = likesResult },
                { throwable -> state.postValue(State.ErrorLikes(entry, throwable)) }
        ))
    }*/
}