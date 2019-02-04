package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Comment (
    @Expose @SerializedName("author") val author: Author,
    @Expose @SerializedName("date") val date: Long,
    @Expose @SerializedName("dateRFC") val dateRFC: String,
    @Expose @SerializedName("isEdited") val edited: Boolean,
    @Expose @SerializedName("entry") val entry: Entry,
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("isFavorited") val isFavorited: Boolean,
    @Expose @SerializedName("is_pinned") val isPinned: Boolean,
    @Expose @SerializedName("level") val level: Int,
    @Expose @SerializedName("likes") val likes: Likes,
    @Expose @SerializedName("load_more") val loadMore: CommentsLoadMore,
    @Expose @SerializedName("media") val media: ArrayList<Medium>,
    @Expose @SerializedName("replyTo") val replyTo: Int,
    @Expose @SerializedName("source_id") val sourceId: Int,
    @Expose @SerializedName("text") val text: String
): Serializable