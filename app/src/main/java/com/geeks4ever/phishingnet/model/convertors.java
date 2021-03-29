package com.geeks4ever.phishingnet.model;

import androidx.collection.CircularArray;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class convertors {

    static Gson gson = new Gson();

    @TypeConverter
    public static CircularArray<URLmodel> deCode(String data) {

        if (data == null)
            return new CircularArray<>();

        Type listType = new TypeToken<CircularArray<URLmodel>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String enCode(CircularArray<URLmodel> someObjects) {
        return gson.toJson(someObjects);
    }

}
