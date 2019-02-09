package org.michaelbel.tjgram.ui.main

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.michaelbel.tjgram.data.constants.Category
import org.michaelbel.tjgram.data.entity.BooleanResult
import org.michaelbel.tjgram.data.entity.EntriesResult
import org.michaelbel.tjgram.data.entity.LikesResult
import org.michaelbel.tjgram.data.constants.Sorting
import org.michaelbel.tjgram.data.remote.TjService
import org.michaelbel.tjgram.data.wss.TjWebSocket

class MainRepository internal constructor(
    private val service: TjService, private val webSocket: TjWebSocket
): MainContract.Repository {

    override fun entries(subsiteId: Long, sorting: String, count: Int, offset: Int): Observable<EntriesResult> {
        //return service.subsiteTimeline(subsiteId, sorting, count, offset).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        //Test.
        return service.timeline(Category.MAINPAGE, Sorting.RECENT, count, offset).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        //return service.userMeFavoritesEntries(count, offset).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun likeEntry(entryId: Int, sign: Int): Observable<LikesResult> {
        return service.likeEntry(entryId, sign).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /*override fun subsite(subsiteId: Long): Observable<BaseResult<Subsite>> {
        return service.subsite(subsiteId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }*/

    override fun complaintEntry(contentId: Int): Observable<BooleanResult> {
        return service.entryComplaint(contentId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun wwsConnect(): Single<TjWebSocket.Open> {
        return webSocket.connect().observeOn(AndroidSchedulers.mainThread())
    }

    override fun wwsDisconnect(): Single<TjWebSocket.Closed> {
        return webSocket.disconnect(1000, "Disconnect").observeOn(AndroidSchedulers.mainThread())
    }

    override fun wwsEventStream(): Flowable<TjWebSocket.Event> {
        return webSocket.eventStream().observeOn(AndroidSchedulers.mainThread())
    }
}