package com.geeks4ever.phishingnet.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.repository.CommonRepository;

public class SettingsViewModel extends AndroidViewModel{

    private final CommonRepository repository;

    private final MutableLiveData<Boolean> FloatingWindowServiceOnOffSetting= new MutableLiveData<>();
    private final MutableLiveData<Boolean> nightMode= new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);

        repository = CommonRepository.getInstance(application);


        repository.getDarkMode().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                nightMode.setValue(aBoolean);
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                FloatingWindowServiceOnOffSetting.setValue(aBoolean);
            }
        });


    }




    //_________________________________________ getters ____________________________________________



    public LiveData<Boolean> getFloatingWindowServiceOnOffSetting() {
        return FloatingWindowServiceOnOffSetting;
    }

    public LiveData<Boolean> getNightMode(){
        return nightMode;
    }




    //___________________________________________ setters __________________________________________


    public void toggleFloatingWindowServiceOnOffSetting() {
        repository.toggleFloatingWindowServiceOnOff();
    }

    public void toggleFloatingWindowServiceOnOffSetting(boolean a) {
        repository.toggleFloatingWindowServiceOnOff();
    }

    public void setNightMode(boolean a){
        repository.setNightMode(a);
    }


}
