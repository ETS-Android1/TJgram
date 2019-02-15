package org.michaelbel.tjgram.modules.addpost

import org.michaelbel.tjgram.BaseContract
import org.michaelbel.tjgram.data.entities.AttachResponse
import org.michaelbel.tjgram.data.entities.Entry
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