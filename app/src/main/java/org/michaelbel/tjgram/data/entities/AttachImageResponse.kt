package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class AttachImageResponse (
    @Expose @SerializedName("type") var type: String,
    @Expose @SerializedName("data") var data: AttachImage
): Serializable