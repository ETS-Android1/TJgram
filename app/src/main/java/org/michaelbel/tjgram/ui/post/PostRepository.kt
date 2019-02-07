package org.michaelbel.tjgram.ui.post

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import org.michaelbel.tjgram.data.entity.AttachResponse
import org.michaelbel.tjgram.data.entity.BaseResult
import org.michaelbel.tjgram.data.entity.EntryResult
import org.michaelbel.tjgram.data.remote.TjService
import java.util.*

class PostRepository internal constructor(
    private val service: TjService) : PostContract.Repository {

    override fun uploadFile(body: MultipartBody.Part): Observable<BaseResult<ArrayList<AttachResponse>>> {
        return service.uploaderUpload(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>): Observable<EntryResult> {
        return service.entryCreate(title, text, subsiteId, attaches).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}