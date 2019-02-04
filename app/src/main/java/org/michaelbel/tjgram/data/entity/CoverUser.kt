package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CoverUser (
    @Expose @SerializedName("cover_url") val coverUrl: String,
    @Expose @SerializedName("height") val height: String,
    @Expose @SerializedName("type") val type: String,
    @Expose @SerializedName("width") val width: String,
    @Expose @SerializedName("y") val y: String
): Serializable