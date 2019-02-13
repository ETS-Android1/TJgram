package org.michaelbel.tjgram.ui.main

import org.michaelbel.tjgram.BaseContract
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.entity.LikesResult

interface MainContract: BaseContract {

    interface View {
        fun addEntries(entries: ArrayList<Entry>, entriesCount: Int)
        fun updateEntries(entries: ArrayList<Entry>)
        fun errorEntries(throwable: Throwable, upd: Boolean)
        fun updateLikes(entry: Entry, likesResult: LikesResult)
        fun updateLikesError(entry: Entry, throwable: Throwable)
        fun complaintSent(status: Boolean)
        //fun sentWssResponse(socket: SocketResponse)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun entries(subsiteId: Long, sorting: String, offset: Int, upd: Boolean)
        fun likeEntry(entry: Entry, sign: Int)
        fun complaintEntry(contentId: Int)
        //fun wwsConnect()
        //fun wwsDisconnect()
        //fun wwsEventStream()
    }
}