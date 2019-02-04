package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdditionalData implements Serializable {
    @Expose @SerializedName("duration") public Double duration;
    @Expose @SerializedName("hasAudio") public boolean isHasAudio;
    @Expose @SerializedName("size") public Integer size;
    @Expose @SerializedName("type") public String type;
    @Expose @SerializedName("url") public String url;
    @Expose @SerializedName("uuid") public String uuid;
}