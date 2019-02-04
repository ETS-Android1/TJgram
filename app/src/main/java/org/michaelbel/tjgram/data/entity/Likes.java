package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Likes implements Serializable {
    @Expose @SerializedName("count") public int count;
    @Expose @SerializedName("is_hidden") public boolean isHidden;
    @Expose @SerializedName("is_liked") public int isLiked;
    @Expose @SerializedName("likers") public HashMap<String, Liker> likersMap;
    @Expose @SerializedName("summ") public int summ;

    public Likes(int count, boolean isHidden, int isLiked, int summ) {
        this.count = count;
        this.isHidden = isHidden;
        this.isLiked = isLiked;
        this.summ = summ;
    }

    public Likes(int summ) {
        this.summ = summ;
    }
}