package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class Counters (
    @Expose @SerializedName("comments") val comments: Long,
    @Expose @SerializedName("entries") val entries: Long,
    @Expose @SerializedName("favorites") val favorites: Long
): Serializable