package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class AdvancedAccessActions (
    @Expose @SerializedName("read_comments") val isReadComments: Boolean,
    @Expose @SerializedName("write_comments") val isWriteComments: Boolean
): Serializable