package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TweetsResult(
    @SerializedName("result") val result: List<Tweet>
) : Serializable