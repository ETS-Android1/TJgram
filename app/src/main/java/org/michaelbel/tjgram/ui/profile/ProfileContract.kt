package org.michaelbel.tjgram.ui.profile

import org.michaelbel.tjgram.BaseContract
import org.michaelbel.tjgram.data.entity.User

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