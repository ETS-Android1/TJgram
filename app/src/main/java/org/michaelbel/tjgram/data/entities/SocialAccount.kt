package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SocialAccount(
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("type") val type: Int,
    @Expose @SerializedName("url") val url: String,
    @Expose @SerializedName("username") val username: String
) : Serializable