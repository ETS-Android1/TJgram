package org.michaelbel.tjgram.ui.newphoto

import io.reactivex.Observable
import okhttp3.MultipartBody
import org.michaelbel.tjgram.data.entity.AttachResponse
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.entity.EntryResult
import org.michaelbel.tjgram.data.entity.BaseResult
import java.io.File
import java.util.*

interface NewEntryContract {

    interface View {
        fun photoUploaded(attach: AttachResponse)
        fun uploadError(throwable: Throwable)
        fun setEntryCreated(entry: Entry)
        fun setError(throwable: Throwable)
    }

    interface Presenter {
        fun setView(view: View)
        fun getView() : View
        fun uploadFile(file: File)
        fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>)
        fun onDestroy()
    }

    interface Repository {
        fun uploadFile(body: MultipartBody.Part) : Observable<BaseResult<ArrayList<AttachResponse>>>
        fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>) : Observable<EntryResult>
    }
}