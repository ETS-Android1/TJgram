package org.michaelbel.tjgram.presentation.features.timeline

import org.michaelbel.tjgram.data.api.results.LikesResult
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.presentation.common.BaseContract

interface TimelineContract {

    interface View: BaseContract.View {
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
        fun entriesNext(subsiteId: Long, sorting: String, offset: Int)
        fun likeEntry(entry: Entry, sign: Int)
        fun complaintEntry(contentId: Int)
        //fun wwsConnect()
        //fun wwsDisconnect()
        //fun wwsEventStream()
    }
}