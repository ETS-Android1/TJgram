package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @Expose @SerializedName("advanced_access") val advancedAccess: AdvancedAccess,
    @Expose @SerializedName("avatar_url") val avatarUrl: String,
    @Expose @SerializedName("counters") val counters: Counters,
    @Expose @SerializedName("cover") val cover: CoverUser,
    @Expose @SerializedName("created") val createdDate: Int,
    @Expose @SerializedName("createdRFC") val createdDateRFC: String,
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("karma") val karma: Long,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("push_topic") val pushTopic: String,
    @Expose @SerializedName("social_accounts") val socialAccounts: List<SocialAccount>,
    @Expose @SerializedName("url") val url: String,
    @Expose @SerializedName("user_hash") val userHash: String
): Serializable