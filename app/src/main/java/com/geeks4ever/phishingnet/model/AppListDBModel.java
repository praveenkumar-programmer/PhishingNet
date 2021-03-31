package com.geeks4ever.phishingnet.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppListTable")
public class AppListDBModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "App")
    public String App;

    public AppListDBModel(@NonNull String App) {
        this.App = App;
    }
}