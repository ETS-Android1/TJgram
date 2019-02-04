package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class AttachImage implements Serializable {
    @Expose @SerializedName("id") public String id;
    @Expose @SerializedName("uuid") public String uuid;
    @Expose @SerializedName("additionalData") public String additionalData;
    @Expose @SerializedName("type") public String type;
    @Expose @SerializedName("color") public String color;
    @Expose @SerializedName("width") public Integer width;
    @Expose @SerializedName("height") public Integer height;
    @Expose @SerializedName("size") public Integer size;
    @Expose @SerializedName("name") public String name;
    @Expose @SerializedName("origin") public String origin;
    @Expose @SerializedName("title") public String title;
    @Expose @SerializedName("description") public String description;
    @Expose @SerializedName("url") public String url;

    @NotNull
    @Override
    public String toString() {
        return "AttachImage{" +
                "id='" + id + '\'' +
                ", uuid='" + uuid + '\'' +
                ", additionalData='" + additionalData + '\'' +
                ", type='" + type + '\'' +
                ", color='" + color + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", size=" + size +
                ", name='" + name + '\'' +
                ", origin='" + origin + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}