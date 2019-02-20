package org.michaelbel.tjgram.presentation.features.profile

import org.michaelbel.tjgram.data.entities.User
import org.michaelbel.tjgram.presentation.common.BaseContract

interface ProfileContract {

    interface View {
        fun setUserMe(user: User, xToken: String)
        fun setAuthError(throwable: Throwable)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun authQr(token: String)
        fun userMe()
    }
}