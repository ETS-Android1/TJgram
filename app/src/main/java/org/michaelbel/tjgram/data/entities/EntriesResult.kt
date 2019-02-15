package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EntriesResult(
    @Expose @SerializedName("result") val results: List<Entry>
) : Serializable