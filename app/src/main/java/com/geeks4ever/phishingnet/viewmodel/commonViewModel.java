package com.geeks4ever.phishingnet.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.repository.CommonRepository;
import com.geeks4ever.phishingnet.services.CheckerService;

import java.util.List;

public class commonViewModel extends AndroidViewModel{

    private final CommonRepository repository;

    private final MutableLiveData<List<URLDBModel>> URLList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> AppList= new MutableLiveData<>();
    private final MutableLiveData<Boolean> mainServiceOnOffSetting= new MutableLiveData<>();
    private final MutableLiveData<Boolean> FloatingWindowServiceOnOffSetting= new MutableLiveData<>();
    private final MutableLiveData<Boolean> nightMode= new MutableLiveData<>();

    public commonViewModel(@NonNull Application application) {
        super(application);

        repository = CommonRepository.getInstance(application);
        repository.getURLList().observeForever(new Observer<List<URLDBModel>>() {
            @Override
            public void onChanged(List<URLDBModel> urldbModels) {
                URLList.setValue(urldbModels);
            }
        });

        getApplication().startService(new Intent(getApplication(), CheckerService.class));

        repository.getDarkMode().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                nightMode.setValue(aBoolean);
            }
        });

        repository.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mainServiceOnOffSetting.setValue(aBoolean);
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                FloatingWindowServiceOnOffSetting.setValue(aBoolean);
            }
        });

        repository.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
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

    public void addApp(String app) {
        repository.addApp(app);
    }

    public void toggleMainServiceOnOffSetting() {
        Log.e("viewModel onButton", "clicked");
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
