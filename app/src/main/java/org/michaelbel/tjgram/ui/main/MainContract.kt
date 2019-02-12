package org.michaelbel.tjgram.ui.main

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.michaelbel.tjgram.BasePresenter
import org.michaelbel.tjgram.data.entity.BooleanResult
import org.michaelbel.tjgram.data.entity.EntriesResult
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.entity.LikesResult
import org.michaelbel.tjgram.data.wss.TjWebSocket
import org.michaelbel.tjgram.data.wss.model.SocketResponse

interface MainContract {

    interface View {
        fun addEntries(entries: ArrayList<Entry>, entriesCount: Int)
        fun updateEntries(entries: ArrayList<Entry>)
        fun errorEntries(throwable: Throwable, upd: Boolean)
        fun updateLikes(entry: Entry, likesResult: LikesResult)
        fun updateLikesError(entry: Entry, throwable: Throwable)
        fun complaintSent(status: Boolean)
        //fun sentWssResponse(socket: SocketResponse)
    }

    interface Presenter: BasePresenter<View> {
        fun entries(subsiteId: Long, sorting: String, offset: Int, upd: Boolean)
        fun likeEntry(entry: Entry, sign: Int)
        fun complaintEntry(contentId: Int)
        //fun wwsConnect()
        //fun wwsDisconnect()
        //fun wwsEventStream()
    }

    interface Repository {
        fun entries(subsiteId: Long, sorting: String, count: Int, offset: Int) : Observable<EntriesResult>
        fun likeEntry(entryId: Int, sign: Int) : Observable<LikesResult>
        fun complaintEntry(contentId: Int) : Observable<BooleanResult>
        //fun wwsConnect(): Single<TjWebSocket.Open>
        //fun wwsDisconnect(): Single<TjWebSocket.Closed>
        //fun wwsEventStream(): Flowable<TjWebSocket.Event>
    }
}