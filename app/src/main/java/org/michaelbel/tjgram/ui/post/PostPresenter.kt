package org.michaelbel.tjgram.ui.post

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.michaelbel.tjgram.data.remote.TjService
import java.io.File

class PostPresenter(private val service: TjService) : PostContract.Presenter {

    private var view: PostContract.View? = null
    private val disposables = CompositeDisposable()

    override fun create(view: PostContract.View) {
        this.view = view
    }

    override fun uploadFile(file: File?) {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file!!)
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

        disposables.add(service.uploaderUpload(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ baseResult ->
            val attaches = baseResult.result
            val attach = attaches!![0]
            view!!.photoUploaded(attach)
        }, { throwable -> view!!.uploadError(throwable) }))
    }

    override fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>) {
        disposables.add(service.entryCreate(title, text, subsiteId, attaches).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { entryResult -> view!!.setEntryCreated(entryResult.result!!) },
            { throwable -> view!!.setError(throwable) })
        )
    }

    override fun destroy() {
        disposables.dispose()
    }
}