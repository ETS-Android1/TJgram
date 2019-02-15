package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Subscription (
    @Expose @SerializedName("is_active") val isActive: Boolean,
    @Expose @SerializedName("active_until") val activeUntil: Long
): Serializable