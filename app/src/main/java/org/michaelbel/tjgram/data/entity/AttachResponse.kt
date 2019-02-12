package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class AttachResponse (
    @Expose @SerializedName("type") var type: String,
    @Expose @SerializedName("data") var data: Attach
): Serializable