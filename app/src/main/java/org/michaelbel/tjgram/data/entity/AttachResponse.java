package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AttachResponse implements Serializable {
    @Expose @SerializedName("type") public String type;
    @Expose @SerializedName("data") public Attach data;
}