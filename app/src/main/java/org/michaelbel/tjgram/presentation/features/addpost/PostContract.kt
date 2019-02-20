package org.michaelbel.tjgram.presentation.features.addpost

import org.michaelbel.tjgram.data.entities.AttachResponse
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.presentation.common.BaseContract
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