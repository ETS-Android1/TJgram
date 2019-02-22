package org.michaelbel.tjgram.data.wss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("all")
public class SocketResponse implements Serializable {

    @Expose @SerializedName("type") public String type;
    @Expose @SerializedName("content_id") public long contentId;
    @Expose @SerializedName("count") public long count;
    @Expose @SerializedName("id") public long id;
    @Expose @SerializedName("state") public long state;
    @Expose @SerializedName("user_hash") public String userHash;

    public SocketResponse(String type, long contentId, long count, long id, long state, String userHash) {
        this.type = type;
        this.contentId = contentId;
        this.count = count;
        this.id = id;
        this.state = state;
        this.userHash = userHash;
    }
}