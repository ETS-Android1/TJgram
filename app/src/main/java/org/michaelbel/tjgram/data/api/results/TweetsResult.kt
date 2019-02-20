package org.michaelbel.tjgram.data.api.results

import com.google.gson.annotations.SerializedName
import org.michaelbel.tjgram.data.entities.Tweet
import java.io.Serializable

@Suppress("unused")
data class TweetsResult(
    @SerializedName("result") val result: List<Tweet>
) : Serializable {

    object Mode {
        const val TWEETS_FRESH = "fresh"
        const val TWEETS_DAY = "day"
        const val TWEETS_WEEK = "week"
        const val TWEETS_MONTH = "month"
    }
}