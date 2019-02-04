package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Medium implements Serializable {
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    @Expose @SerializedName("additionalData") public AdditionalData additionalData;
    @Expose @SerializedName("iframeUrl") public String iframeUrl;
    @Expose @SerializedName("imageUrl") public String imageUrl;
    @Expose @SerializedName("service") public String service;
    @Expose @SerializedName("size") public Size size;
    @Expose @SerializedName("type") public Integer type;
}