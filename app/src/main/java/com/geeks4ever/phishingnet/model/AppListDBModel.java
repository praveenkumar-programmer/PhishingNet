package com.geeks4ever.phishingnet.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppListTable")
public class AppListDBModel {

    @PrimaryKey(autoGenerate = true)
    public int key;

    @ColumnInfo(name = "App")
    public String App;

    public AppListDBModel(String App) {
        this.App = App;
    }
}