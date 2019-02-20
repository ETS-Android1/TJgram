package org.michaelbel.tjgram.data.api.results

import org.michaelbel.tjgram.data.entities.BaseResult
import org.michaelbel.tjgram.data.entities.LikesForResult

class LikesResult : BaseResult<LikesForResult>() {

    object Status {
        const val LIKED = 1
        const val NEUTRAL = 0
        const val DISLIKED = -1
    }
}