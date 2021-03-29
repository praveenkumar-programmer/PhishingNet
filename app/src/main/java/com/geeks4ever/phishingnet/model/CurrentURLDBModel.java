package com.geeks4ever.phishingnet.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "currentURLTable")
public class CurrentURLDBModel {

    @PrimaryKey
    public int key;

    @ColumnInfo(name = "url")
    public String url;

    public CurrentURLDBModel(int key, String url) {
        this.key = key;
        this.url = url;
    }
}