package com.geeks4ever.phishingnet.model.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.geeks4ever.phishingnet.model.AppListDBModel;
import com.geeks4ever.phishingnet.model.CurrentURLDBModel;
import com.geeks4ever.phishingnet.model.SettingsDBModel;
import com.geeks4ever.phishingnet.model.URLDBModel;

import java.util.List;

import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface CommonDAO {

    @Insert (onConflict = REPLACE)
    void setURLList(URLDBModel mode);

    @Insert (onConflict = REPLACE)
    void setApp(AppListDBModel app);

    @Delete
    void removeApp(AppListDBModel app);

    @Insert (onConflict = REPLACE)
    void setCurrentURL(CurrentURLDBModel url);

    @Insert (onConflict = REPLACE)
    void setSetting(SettingsDBModel settingsDBModel);

    @Query("Select * from URLTable where `key`= 0")
    Flowable<List<URLDBModel>> getURLList();

    @Query("Select App from applisttable")
    Flowable<List<String>> getAppList();

    @Query("Select url from currentURLTable")
    Flowable<List<String>> getCurrentUrl();

    @Query( "Select value from SettingsTable where setting = :setting " )
    Flowable<Boolean> getSetting(String setting);


}
