package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class LikesForResult (
    @Expose @SerializedName("count") var count: Int,
    @Expose @SerializedName("is_hidden") var isHidden: Boolean,
    @Expose @SerializedName("is_liked") var isLiked: Int,
    @Expose @SerializedName("likers") var likers: HashMap<Int, Int>,
    @Expose @SerializedName("summ") var summ: Int
): Serializable