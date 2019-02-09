package org.michaelbel.tjgram.data.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "karma")
    public long karma;

    @ColumnInfo(name = "date_rfc")
    public String createdDateRFC;

    @ColumnInfo(name = "date")
    public int createdDate;

    public String avatarUrl;

    public String pushTopic;

    @ColumnInfo(name = "url")
    public String url;

    public String userHash;

    public User(int id, String name, long karma, String createdDateRFC, int createdDate, String avatarUrl, String pushTopic, String url, String userHash) {
        this.id = id;
        this.name = name;
        this.karma = karma;
        this.createdDateRFC = createdDateRFC;
        this.createdDate = createdDate;
        this.avatarUrl = avatarUrl;
        this.pushTopic = pushTopic;
        this.url = url;
        this.userHash = userHash;
    }
}