package org.michaelbel.tjgram.presentation.features.profile

import androidx.lifecycle.MutableLiveData
import org.michaelbel.tjgram.presentation.common.BaseViewModel
import org.michaelbel.tjgram.presentation.common.SingleLiveEvent

class ProfileViewModel(private val authUser: AuthUser) : BaseViewModel() {

    var viewState: MutableLiveData<ProfileViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<Throwable?> = SingleLiveEvent()

    init {
        viewState.value = ProfileViewState()
    }

    fun getUser() {
        addDisposable(authUser.observable()
                .subscribe({ userResult ->
                    viewState.value?.let {
                        val newState = this.viewState.value?.copy(showLoading = false, user = userResult.body()?.result)
                        this.viewState.value = newState
                        this.errorState.value = null
                    }

                }, {
                    viewState.value = viewState.value?.copy(showLoading = false)
                    errorState.value = it
                }))
    }
}