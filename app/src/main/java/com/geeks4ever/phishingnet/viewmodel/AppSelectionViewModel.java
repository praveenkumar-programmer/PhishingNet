package com.geeks4ever.phishingnet.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.repository.CommonRepository;
import com.geeks4ever.phishingnet.services.CheckerService;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionViewModel extends AndroidViewModel{

    private final CommonRepository repository;

    private final MutableLiveData<List<String>> AppList= new MutableLiveData<>();

    public AppSelectionViewModel(@NonNull Application application) {
        super(application);

        repository = CommonRepository.getInstance(application);

        getApplication().startService(new Intent(getApplication(), CheckerService.class));

        repository.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                AppList.setValue(strings);
            }
        });


    }


    //_________________________________________ getters ____________________________________________



    public LiveData<List<String>> getAppList() {
        return AppList;
    }



    //___________________________________________ setters __________________________________________

    public void addApp(String app) {
        repository.addApp(app);
    }

    public void removeApp(String app){
        repository.removeApp(app);
    }

    public void addAllApps(ArrayList<String> apps){
        repository.addAllApps(apps);
    }

    public void removeAllApps(ArrayList<String> apps){
        repository.removeAllApps(apps);
    }


}
