package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tweet(
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("text") val text: String,
    @Expose @SerializedName("user") val user: TweetUser,
    @Expose @SerializedName("retweet_count") val retweetCount: Long,
    @Expose @SerializedName("favorite_count") val favoriteCount: Long,
    @Expose @SerializedName("has_media") val hasMedia: Boolean,
    @Expose @SerializedName("media") val media: List<TweetMedia>,
    @Expose @SerializedName("created_at") val createdAt: Long
) : Serializable