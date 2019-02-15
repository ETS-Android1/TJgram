package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Likes (
    @Expose @SerializedName("count") var count: Int = 0,
    @Expose @SerializedName("is_hidden") var isHidden: Boolean = false,
    @Expose @SerializedName("is_liked") var isLiked: Int = 0,
    @Expose @SerializedName("likers") var likersMap: HashMap<String, Liker>? = null,
    @Expose @SerializedName("summ") var summ: Int = 0
): Serializable