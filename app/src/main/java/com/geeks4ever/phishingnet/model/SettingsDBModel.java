package com.geeks4ever.phishingnet.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SettingsTable")
public class SettingsDBModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "setting")
    public String key;

    @ColumnInfo(name = "value")
    public Boolean value;

    public SettingsDBModel(String key, Boolean value) {
        this.key = key;
        this.value = value;
    }
}