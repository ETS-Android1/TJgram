package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class Subsite (
    @Expose @SerializedName("avatar_url") var avatarUrl: String,
    @Expose @SerializedName("comments_count") var commentsCount: Int,
    @Expose @SerializedName("contacts") var contacts: SubsiteContacts,
    //@Expose @SerializedName("cover") var cover: SubsiteCover,
    @Expose @SerializedName("createdDate") var created: Long,
    @Expose @SerializedName("createdDateRFC") var createdRFC: String,
    @Expose @SerializedName("description") var description: String,
    @Expose @SerializedName("entries_count") var entriesCount: Int,
    @Expose @SerializedName("id") var id: Long,
    @Expose @SerializedName("is_enable_writing") var isEnableWriting: Boolean,
    @Expose @SerializedName("is_muted") var isMuted: Boolean,
    @Expose @SerializedName("is_subscribed") var isSubscribed: Boolean,
    @Expose @SerializedName("is_unsubscribable") var isUnsubscribable: Boolean,
    @Expose @SerializedName("is_verified") var isVerified: Boolean,
    @Expose @SerializedName("karma") var karma: Int,
    @Expose @SerializedName("name") var name: String,
    @Expose @SerializedName("rules") var rules: String,
    @Expose @SerializedName("subscribers_count") var subscribersCount: Int,
    @Expose @SerializedName("type") var type: Long,
    @Expose @SerializedName("url") var url: String,
    @Expose @SerializedName("vacancies_count") var vacanciesCount: Int
): Serializable