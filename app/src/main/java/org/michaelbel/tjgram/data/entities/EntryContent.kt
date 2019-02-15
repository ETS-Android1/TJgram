package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EntryContent (
    @Expose @SerializedName("html") val html: String,
    @Expose @SerializedName("version") val version: String
): Serializable