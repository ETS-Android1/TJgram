package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TweetUser(
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("followers_count") val followersCount: Long,
    @SerializedName("friends_count") val friendsCount: Int,
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("profile_image_url") val profileImageUrl: String,
    @SerializedName("profile_image_url_bigger") val profileImageUrlBigger: String,
    @SerializedName("screen_name") val screenName: String,
    @SerializedName("statuses_count") val statusesCount: Long
) : Serializable