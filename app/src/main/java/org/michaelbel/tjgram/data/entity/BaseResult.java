package org.michaelbel.tjgram.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResult<T> implements Serializable {
    @Expose @SerializedName("result") public T result;
    @Expose @SerializedName("message") public String message;
}