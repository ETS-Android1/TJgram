package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Medium (
    @Expose @SerializedName("additionalData") var additionalData: AdditionalData,
    @Expose @SerializedName("iframeUrl") var iframeUrl: String,
    @Expose @SerializedName("imageUrl") var imageUrl: String,
    @Expose @SerializedName("service") var service: String,
    @Expose @SerializedName("size") var size: Size,
    @Expose @SerializedName("type") var type: Int
): Serializable