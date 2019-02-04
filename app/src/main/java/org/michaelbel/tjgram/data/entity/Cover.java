package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cover implements Serializable {
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    @Expose @SerializedName("additionalData") public AdditionalData additionalData;
    @Expose @SerializedName("size") public Size size;
    @Expose @SerializedName("size_simple") public String sizeSimple;
    @Expose @SerializedName("thumbnailUrl") public String thumbnailUrl;
    @Expose @SerializedName("type") public Integer type;
    @Expose @SerializedName("url") public String url;
}