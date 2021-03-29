package com.geeks4ever.phishingnet.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.URLmodel;
import com.geeks4ever.phishingnet.model.repository.CommonRepository;
import com.geeks4ever.phishingnet.services.CheckerService;

import java.util.List;

public class commonViewModel extends AndroidViewModel{

    private CommonRepository repository;


    private MutableLiveData<List<URLDBModel>> URLList = new MutableLiveData<>();
    private MutableLiveData<List<String>> AppList= new MutableLiveData<>();
    private MutableLiveData<Boolean> mainServiceOnOffSetting= new MutableLiveData<>();
    private MutableLiveData<Boolean> FloatingWindowServiceOnOffSetting= new MutableLiveData<>();
    private MutableLiveData<Boolean> nightMode= new MutableLiveData<>();

    public commonViewModel(@NonNull Application application) {
        super(application);

        repository = CommonRepository.getInstance(application);
        repository.getURLList().observeForever(new Observer<List<URLDBModel>>() {
            @Override
            public void onChanged(List<URLDBModel> urldbModels) {
                if(urldbModels != null)
                    URLList.setValue(urldbModels);
            }
        });

        getApplication().startService(new Intent(getApplication(), CheckerService.class));

        repository.getDarkMode().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    nightMode.setValue(aBoolean);
            }
        });

        repository.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    mainServiceOnOffSetting.setValue(aBoolean);
                }
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    FloatingWindowServiceOnOffSetting.setValue(aBoolean);
                }
            }
        });

        repository.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null)
                    AppList.setValue(strings);
            }
        });



    }









    //_________________________________________ getters ____________________________________________



    public LiveData<List<URLDBModel>> getURLList() {
        return URLList;
    }

    public LiveData<List<String>> getAppList() {
        return AppList;
    }

    public LiveData<Boolean> getMainServiceOnOffSetting() {
        return mainServiceOnOffSetting;
    }

    public LiveData<Boolean> getFloatingWindowServiceOnOffSetting() {
        return FloatingWindowServiceOnOffSetting;
    }

    public LiveData<Boolean> getNightMode(){
        return nightMode;
    }




    //___________________________________________ setters __________________________________________


    public void setURLList( CircularArray<URLmodel> URLList) {
        repository.setURLList(URLList);
    }

    public void addApp(String app) {
        repository.addApp(app);
    }

    public void toggleMainServiceOnOffSetting() {
        repository.toggleMainServiceOnOff();
    }

    public void toggleFloatingWindowServiceOnOffSetting() {
        repository.toggleFloatingWindowServiceOnOff();

    }public void toggleMainServiceOnOffSetting(boolean a) {
        repository.toggleMainServiceOnOff();
    }

    public void toggleFloatingWindowServiceOnOffSetting(boolean a) {
        repository.toggleFloatingWindowServiceOnOff();
    }

    public void removeApp(String app){
        repository.removeApp(app);
    }

    public void setNightMode(boolean a){
        repository.setNightMode(a);
    }


}
