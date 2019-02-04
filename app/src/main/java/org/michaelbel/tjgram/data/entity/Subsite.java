package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Subsite implements Serializable {
    public static final int TYPE_COMPANY = 3;
    public static final int TYPE_SECTION = 2;
    public static final int TYPE_USER = 1;

    @Expose @SerializedName("avatar_url") public String avatarUrl;
    @Expose @SerializedName("comments_count") public int commentsCount;
    @Expose @SerializedName("contacts") public SubsiteContacts contacts;
    //@Expose @SerializedName("cover") public SubsiteCover cover;
    @Expose @SerializedName("created") public long created;
    @Expose @SerializedName("createdRFC") public String createdRFC;
    @Expose @SerializedName("description") public String description;
    @Expose @SerializedName("entries_count") public int entriesCount;
    @Expose @SerializedName("id") public long id;
    @Expose @SerializedName("is_enable_writing") public boolean isEnableWriting;
    @Expose @SerializedName("is_muted") public boolean isMuted;
    @Expose @SerializedName("is_subscribed") public boolean isSubscribed;
    @Expose @SerializedName("is_unsubscribable") public boolean isUnsubscribable;
    @Expose @SerializedName("is_verified") public boolean isVerified;
    @Expose @SerializedName("karma") public int karma;
    @Expose @SerializedName("name") public String name;
    @Expose @SerializedName("rules") public String rules;
    @Expose @SerializedName("subscribers_count") public int subscribersCount;
    @Expose @SerializedName("type") public long type;
    @Expose @SerializedName("url") public String url;
    @Expose @SerializedName("vacancies_count") public int vacanciesCount;
}