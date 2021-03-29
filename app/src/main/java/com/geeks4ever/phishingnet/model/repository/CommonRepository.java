package com.geeks4ever.phishingnet.model.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.collection.CircularArray;
import androidx.lifecycle.LiveData;

import com.geeks4ever.phishingnet.model.AppListDBModel;
import com.geeks4ever.phishingnet.model.CurrentURLDBModel;
import com.geeks4ever.phishingnet.model.SettingsDBModel;
import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.URLmodel;

import java.util.List;

public class CommonRepository {

    private static CommonRepository repository;

    private CommonDAO commonDAO;
    private LiveData<List<URLDBModel>> URLList;
    private LiveData<List<String>> AppList;
    private LiveData<List<String>> currentUrl;
    private LiveData<Boolean> mainServiceOnOffSetting;
    private LiveData<Boolean> FloatingWindowServiceOnOffSetting;
    private LiveData<Boolean> darkMode;

    private Context context;

    private CommonRepository(Application application){

        context = application;
        commonDAO = CommonDatabase.getInstance(application).commonDAO();
        URLList = commonDAO.getURLList();
        AppList = commonDAO.getAppList();
        currentUrl = commonDAO.getCurrentUrl();
        mainServiceOnOffSetting = commonDAO.getSetting("mainService");
        FloatingWindowServiceOnOffSetting = commonDAO.getSetting("floatingWindowService");
        darkMode = commonDAO.getSetting("nightMode");

    }

    public static CommonRepository getInstance(Application application) {

        if(repository == null)
            repository = new CommonRepository(application);
        return repository;
    }






    //_________________________________________ methods ____________________________________________


    public void addURL(URLmodel url){

        CircularArray<URLmodel> array = new CircularArray<>();

        if(!(URLList.getValue() == null || URLList.getValue().isEmpty()))
            array = URLList.getValue().get(0).URLList;

        if(array.size() > 100)
            array.removeFromEnd(1);
        array.addFirst(url);

        setURLList(array);

    }

    public void addApp(String app){
        new AddAppAsyncTask(commonDAO).execute(app);
    }

    public void removeApp(String app){
        new RemoveAppAsyncTask(commonDAO).execute(app);
    }

    public  void setNightMode(boolean a){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                commonDAO.setSetting(new SettingsDBModel("nightMode", a));
            }
        });
    }

    public void toggleMainServiceOnOff(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                boolean b = mainServiceOnOffSetting.getValue() != null && !mainServiceOnOffSetting.getValue();
                commonDAO.setSetting(new SettingsDBModel("mainService", b));
            }
        });
    }

    public void toggleFloatingWindowServiceOnOff(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                commonDAO.setSetting(new SettingsDBModel("floatingWindowService",
                        (FloatingWindowServiceOnOffSetting.getValue() != null) && !FloatingWindowServiceOnOffSetting.getValue()));
            }
        });

    }

    public void toggleMainServiceOnOff(boolean a){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                commonDAO.setSetting(new SettingsDBModel("mainService", a));
            }
        });
    }

    public void toggleFloatingWindowServiceOnOff(boolean a){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                commonDAO.setSetting(new SettingsDBModel("floatingWindowService", a));
            }
        });

    }




    //_________________________________________ setters ____________________________________________

    public void setURLList(CircularArray<URLmodel> array){
        new SetURLListAsyncTask(commonDAO).execute(new URLDBModel(0, array));
    }

    public void setCurrentUrl(String string){
        new setCurrentURLAsyncTask(commonDAO).execute(string);
    }



    private static class RemoveAppAsyncTask extends AsyncTask<String, Void, Void> {

        private CommonDAO dao;

        private RemoveAppAsyncTask(CommonDAO modeDAO){
            this.dao = modeDAO;
        }

        @Override
        protected Void doInBackground(String... items) {
            dao.removeApp(new AppListDBModel(items[0]));
            return null;
        }
    }

    private static class AddAppAsyncTask extends AsyncTask<String, Void, Void> {

        private CommonDAO dao;

        private AddAppAsyncTask(CommonDAO modeDAO){
            this.dao = modeDAO;
        }

        @Override
        protected Void doInBackground(String... items) {
            dao.setApp(new AppListDBModel(items[0]));
            return null;
        }
    }

    private static class SetURLListAsyncTask extends AsyncTask<URLDBModel, Void, Void> {

        private CommonDAO dao;

        private SetURLListAsyncTask(CommonDAO modeDAO){
            this.dao = modeDAO;
        }

        @Override
        protected Void doInBackground(URLDBModel... items) {
            dao.setURLList(items[0]);
            return null;
        }
    }

    private static class setCurrentURLAsyncTask extends AsyncTask<String, Void, Void> {

        private CommonDAO dao;

        private setCurrentURLAsyncTask(CommonDAO modeDAO){
            this.dao = modeDAO;
        }

        @Override
        protected Void doInBackground(String... items) {
            dao.setCurrentURL(new CurrentURLDBModel(0, items[0]));
            return null;
        }
    }

    //______________________________________ getters _______________________________________________


    public LiveData<List<URLDBModel>> getURLList() {
        return URLList;
    }

    public LiveData<List<String>> getAppList() {
        return AppList;
    }

    public LiveData<List<String>> getCurrentUrl() {
        return currentUrl;
    }

    public LiveData<Boolean> getMainServiceOnOffSetting() {
        return mainServiceOnOffSetting;
    }

    public LiveData<Boolean> getFloatingWindowServiceOnOffSetting() {
        return FloatingWindowServiceOnOffSetting;
    }

    public LiveData<Boolean> getDarkMode() {
        return darkMode;
    }




}
