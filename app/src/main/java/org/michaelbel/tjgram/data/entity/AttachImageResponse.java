package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class AttachImageResponse implements Serializable {
    @Expose @SerializedName("type") public String type;
    @Expose @SerializedName("data") public AttachImage data;

    @NotNull
    @Override
    public String toString() {
        return "AttachImageResponse{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}