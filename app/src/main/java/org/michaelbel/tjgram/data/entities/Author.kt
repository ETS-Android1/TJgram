package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Author (
    @Expose @SerializedName("avatar_url") val avatarUrl: String,
    @Expose @SerializedName("createdDate") val created: Int,
    @Expose @SerializedName("first_name") val firstName: String,
    @Expose @SerializedName("gender") val gender: Int,
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("karma") val karma: Int,
    @Expose @SerializedName("last_name") val lastName: String,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("social_accounts") val socialAccounts: ArrayList<SocialAccount>,
    @Expose @SerializedName("url") val url: String,
    @Expose @SerializedName("work") val work: Any
): Serializable