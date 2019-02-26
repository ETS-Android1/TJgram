package org.michaelbel.tjgram.presentation.features.addpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.entities.AttachResponse
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.domain.usecases.CreatePost
import org.michaelbel.tjgram.presentation.base.BaseVM
import java.io.File

class PostVM(private val service: TjApi, private val createPost: CreatePost): BaseVM() {

    private val _attaches = MutableLiveData<AttachResponse>()
    val attaches: LiveData<AttachResponse>
        get() = _attaches

    private val _attachError = MutableLiveData<String>()
    val attachError: LiveData<String>
        get() = _attachError

    private val _entryCreate = MutableLiveData<Entry>()
    val entryCreate: LiveData<Entry>
        get() = _entryCreate

    private val _entryError = MutableLiveData<String>()
    val entryError: LiveData<String>
        get() = _entryError

    fun uploadFile(file: File?) {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file!!)
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

        disposable.add(service.uploaderUpload(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ baseResult ->
            val attaches = baseResult.result
            val attach = attaches!![0]
            _attaches.value = attach
        }, { throwable -> _attachError.value = throwable.message }))
    }

    fun createEntry(title: String, text: String, subsiteId: Long, attaches: Map<String, String>) {
        disposable.add(createPost.createEntry(title, text, subsiteId, attaches)
                .subscribe({ _entryCreate.value = it }, { _entryError.value = it.message })
        )
    }
}