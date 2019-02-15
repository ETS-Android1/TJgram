package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Badge (
    @Expose @SerializedName("background") val background: String,
    @Expose @SerializedName("border") val border: String,
    @Expose @SerializedName("color") val color: String,
    @Expose @SerializedName("text") val text: String,
    @Expose @SerializedName("type") val type: String
): Serializable