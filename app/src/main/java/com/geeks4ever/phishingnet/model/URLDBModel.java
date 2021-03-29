package com.geeks4ever.phishingnet.model;

import androidx.collection.CircularArray;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "URLTable")
@TypeConverters(convertors.class)
public class URLDBModel {

    @PrimaryKey
    public int key;

    @ColumnInfo(name = "URLList")
    public CircularArray<URLmodel> URLList;

    public URLDBModel(int key, CircularArray<URLmodel> URLList) {
        this.key = key;
        this.URLList = URLList;
    }
}