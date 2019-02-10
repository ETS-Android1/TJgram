package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class AttachImage (
    @Expose @SerializedName("id") var id: String,
    @Expose @SerializedName("uuid") var uuid: String,
    @Expose @SerializedName("additionalData") var additionalData: String,
    @Expose @SerializedName("type") var type: String,
    @Expose @SerializedName("color") var color: String,
    @Expose @SerializedName("width") var width: Int,
    @Expose @SerializedName("height") var height: Int,
    @Expose @SerializedName("size") var size: Int,
    @Expose @SerializedName("name") var name: String,
    @Expose @SerializedName("origin") var origin: String,
    @Expose @SerializedName("title") var title: String,
    @Expose @SerializedName("description") var description: String,
    @Expose @SerializedName("url") var url: String
): Serializable