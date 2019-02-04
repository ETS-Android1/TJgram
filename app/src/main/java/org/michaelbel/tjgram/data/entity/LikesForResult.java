package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class LikesForResult implements Serializable {
    @Expose @SerializedName("count") public int count;
    @Expose @SerializedName("is_hidden") public boolean isHidden;
    @Expose @SerializedName("is_liked") public int isLiked;
    @Expose @SerializedName("likers") public HashMap<Integer, Integer> likers;
    @Expose @SerializedName("summ") public int summ;
}