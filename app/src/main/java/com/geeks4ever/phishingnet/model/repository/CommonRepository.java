package com.geeks4ever.phishingnet.model.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.collection.CircularArray;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.AppListDBModel;
import com.geeks4ever.phishingnet.model.CurrentURLDBModel;
import com.geeks4ever.phishingnet.model.SettingsDBModel;
import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.URLmodel;

import java.util.List;

public class CommonRepository {

    private static CommonRepository repository;

    private final CommonDAO commonDAO;
    private final MutableLiveData<List<URLDBModel>> URLList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> AppList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> currentUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mainServiceOnOffSetting = new MutableLiveData<>();
    private final MutableLiveData<Boolean> FloatingWindowServiceOnOffSetting = new MutableLiveData<>();
    private final MutableLiveData<Boolean> darkMode = new MutableLiveData<>();

    private CommonRepository(Application application){

        commonDAO = CommonDatabase.getInstance(application).commonDAO();

        commonDAO.getURLList().observeForever(new Observer<List<URLDBModel>>() {
            @Override
            public void onChanged(List<URLDBModel> urldbModels) {
                if(urldbModels != null && !urldbModels.isEmpty())
                    URLList.setValue(urldbModels);
            }
        });

        commonDAO.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null && !strings.isEmpty())
                    AppList.setValue(strings);
            }
        });

        commonDAO.getCurrentUrl().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null && !strings.isEmpty())
                    currentUrl.setValue(strings);
            }
        });

        commonDAO.getSetting("mainService").observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    mainServiceOnOffSetting.setValue(aBoolean);
            }
        });

        commonDAO.getSetting("floatingWindowService").observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    FloatingWindowServiceOnOffSetting.setValue(aBoolean);
            }
        });

        commonDAO.getSetting("nightMode").observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    darkMode.setValue(aBoolean);
            }
        });

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
