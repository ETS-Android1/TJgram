package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TweetMedia(
    @Expose @SerializedName("type") val type: Long,
    @Expose @SerializedName("thumbnail_url") val thumbnail_Url: String,
    @Expose @SerializedName("media_url") val mediaUrl: String,
    @Expose @SerializedName("thumbnail_width") val thumbnailWidth: Long,
    @Expose @SerializedName("thumbnail_height") val thumbnailHeight: Long,
    @Expose @SerializedName("ratio") val ratio: Double
) : Serializable