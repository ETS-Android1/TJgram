package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class AdditionalData (
    @Expose @SerializedName("duration") var duration: Double,
    @Expose @SerializedName("hasAudio") var isHasAudio: Boolean,
    @Expose @SerializedName("size") var size: Int,
    @Expose @SerializedName("type") var type: String,
    @Expose @SerializedName("url") var url: String,
    @Expose @SerializedName("uuid") var uuid: String
): Serializable