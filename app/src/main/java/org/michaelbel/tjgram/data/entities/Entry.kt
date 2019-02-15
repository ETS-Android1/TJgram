package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Entry (
    @Expose @SerializedName("audioUrl") var audioUrl: String? = "",
    @Expose @SerializedName("author") var author: Author? = null,
    @Expose @SerializedName("badges") var badges: ArrayList<Badge>? = null,
    @Expose @SerializedName("commentatorsAvatars") var commentatorsAvatars: ArrayList<String>? = null,
    @Expose @SerializedName("commentsCount") var commentsCount: Int = 0,
    @Expose @SerializedName("commentsPreview") var commentsPreview: ArrayList<Comment>? = null,
    @Expose @SerializedName("cover") var cover: Cover? = null,
    @Expose @SerializedName("date") var date: Int = 0,
    @Expose @SerializedName("dateRFC") var dateRFC: String? = "",
    @Expose @SerializedName("entryContent") var entryContent: EntryContent? = null,
    @Expose @SerializedName("favoritesCount") var favoritesCount: Int = 0,
    @Expose @SerializedName("hitsCount") var hitsCount: Int = 0,
    @Expose @SerializedName("id") var id: Int = 0,
    @Expose @SerializedName("intro") var intro: String = "",
    @Expose @SerializedName("introInFeed") var introInFeed: Any? = null,
    @Expose @SerializedName("isEditorial") var isEditorial: Boolean = false,
    @Expose @SerializedName("isEnabledComments") var isEnabledComments: Boolean = false,
    @Expose @SerializedName("isEnabledLikes") var isEnabledLikes: Boolean = false,
    @Expose @SerializedName("isFavorited") var isFavorited: Boolean = false,
    @Expose @SerializedName("isPinned") var isPinned: Boolean = false,
    @Expose @SerializedName("last_modification_date") var lastModificationDate: Int = 0,
    @Expose @SerializedName("likes") var likes: Likes? = null,
    @Expose @SerializedName("similar") var similar: ArrayList<Similar>? = null,
    @Expose @SerializedName("subsite") var subsite: Subsite? = null,
    @Expose @SerializedName("title") var title: String = "",
    @Expose @SerializedName("type") var type: Int = 0,
    @Expose @SerializedName("webviewUrl") var webviewUrl: String = ""
): Serializable