package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Entry implements Serializable {
    @Expose @SerializedName("audioUrl") public String audioUrl;
    @Expose @SerializedName("author") public Author author;
    @Expose @SerializedName("badges") public ArrayList<Badge> badges;
    @Expose @SerializedName("commentatorsAvatars") public ArrayList<String> commentatorsAvatars;
    @Expose @SerializedName("commentsCount") public int commentsCount;
    @Expose @SerializedName("commentsPreview") public ArrayList<Comment> commentsPreview;
    @Expose @SerializedName("cover") public Cover cover;
    @Expose @SerializedName("date") public Integer date;
    @Expose @SerializedName("dateRFC") public String dateRFC;
    @Expose @SerializedName("entryContent") public EntryContent entryContent;
    @Expose @SerializedName("favoritesCount") public int favoritesCount;
    @Expose @SerializedName("hitsCount") public int hitsCount;
    @Expose @SerializedName("id") public int id;
    @Expose @SerializedName("intro") public String intro;
    @Expose @SerializedName("introInFeed") public Object introInFeed;
    @Expose @SerializedName("isEditorial") public boolean isEditorial;
    @Expose @SerializedName("isEnabledComments") public boolean isEnabledComments;
    @Expose @SerializedName("isEnabledLikes") public boolean isEnabledLikes;
    @Expose @SerializedName("isFavorited") public boolean isFavorited;
    @Expose @SerializedName("isPinned") public boolean isPinned;
    @Expose @SerializedName("last_modification_date") public Integer lastModificationDate;
    @Expose @SerializedName("likes") public Likes likes;
    @Expose @SerializedName("similar") public ArrayList<Similar> similar;
    @Expose @SerializedName("subsite") public Subsite subsite;
    @Expose @SerializedName("title") public String title;
    @Expose @SerializedName("type") public Integer type;
    @Expose @SerializedName("webviewUrl") public String webviewUrl;

    public Entry(int id, Likes likes) {
        this.id = id;
        this.likes = likes;
    }
}