package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CommentsLoadMore (
    @Expose @SerializedName("avatars") val avatars: ArrayList<String>,
    @Expose @SerializedName("count") val count: Int,
    @Expose @SerializedName("ids") val ids: List<Int>
): Serializable