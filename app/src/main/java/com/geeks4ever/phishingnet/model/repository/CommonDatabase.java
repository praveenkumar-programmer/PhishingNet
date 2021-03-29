package com.geeks4ever.phishingnet.model.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.geeks4ever.phishingnet.model.AppListDBModel;
import com.geeks4ever.phishingnet.model.CurrentURLDBModel;
import com.geeks4ever.phishingnet.model.SettingsDBModel;
import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.convertors;


@Database(entities = {URLDBModel.class, AppListDBModel.class, CurrentURLDBModel.class,
        SettingsDBModel.class}, version = 1, exportSchema = false)
@TypeConverters(convertors.class)
public abstract class CommonDatabase extends RoomDatabase {

    private static CommonDatabase db;

    public abstract CommonDAO commonDAO();

    public static synchronized CommonDatabase getInstance(Context context){

        if(db == null){
            db = Room.databaseBuilder(context.getApplicationContext(),
                    CommonDatabase.class, "common_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return db;
    }


}
