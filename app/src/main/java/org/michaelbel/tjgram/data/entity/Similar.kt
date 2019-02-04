package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class Similar(
    @Expose @SerializedName("date") val date: Int,
    @Expose @SerializedName("dateRFC") val dateRFC: String,
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("isAdvertisement") val isAdvertisement: Boolean,
    @Expose @SerializedName("title") val title: String,
    @Expose @SerializedName("url") val url: String
) : Serializable