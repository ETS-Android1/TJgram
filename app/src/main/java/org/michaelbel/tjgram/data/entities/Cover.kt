package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class Cover (
    @Expose @SerializedName("additionalData") var additionalData: AdditionalData,
    @Expose @SerializedName("size") var size: Size,
    @Expose @SerializedName("size_simple") var sizeSimple: String,
    @Expose @SerializedName("thumbnailUrl") var thumbnailUrl: String,
    @Expose @SerializedName("type") var type: Int,
    @Expose @SerializedName("url") var url: String
): Serializable {

    fun isImage(): Boolean = type == 1

    fun isVideo(): Boolean = type == 2
}