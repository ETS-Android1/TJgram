package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class AdvancedAccess (
    @Expose @SerializedName("hash") val hash: String,
    @Expose @SerializedName("actions") val actions: AdvancedAccessActions,
    @Expose @SerializedName("tj_subscription") val tjSubscription: Subscription,
    @Expose @SerializedName("is_needs_advanced_access") val isNeedsAdvancedAccess: Boolean
): Serializable