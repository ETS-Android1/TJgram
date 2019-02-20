package org.michaelbel.tjgram.data.api.results

import org.michaelbel.tjgram.data.entities.BaseResult
import org.michaelbel.tjgram.data.entities.User

@Suppress("unused")
class UserResult : BaseResult<User>() {

    object Accounts {
        const val ACCOUNT_VK = 1
        const val ACCOUNT_TWITTER = 2
        const val ACCOUNT_FACEBOOK = 3
        const val ACCOUNT_GOOGLE = 4
    }
}