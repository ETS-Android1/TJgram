package org.michaelbel.tjgram.ui.post

import org.michaelbel.tjgram.ui.base.BaseContract
import org.michaelbel.tjgram.data.entity.AttachResponse
import org.michaelbel.tjgram.data.entity.Entry
import java.io.File

interface PostContract {

    interface View {
        fun photoUploaded(attach: AttachResponse)
        fun uploadError(throwable: Throwable)
        fun setEntryCreated(entry: Entry)
        fun setError(throwable: Throwable)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun uploadFile(file: File?)
        fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>)
    }
}