package com.geeks4ever.phishingnet.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.repository.CommonRepository;


public class HomePageViewModel extends AndroidViewModel{

    private final CommonRepository repository;

    private final MutableLiveData<Boolean> mainServiceOnOffSetting= new MutableLiveData<>();

    public HomePageViewModel(@NonNull Application application) {
        super(application);

        repository = CommonRepository.getInstance(application);

        repository.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mainServiceOnOffSetting.setValue(aBoolean);
            }
        });



    }






    //_________________________________________ getters ____________________________________________



    public LiveData<Boolean> getMainServiceOnOffSetting() {
        return mainServiceOnOffSetting;
    }



    //___________________________________________ setters __________________________________________



    public void toggleMainServiceOnOffSetting() {
//        Log.e("viewModel onButton", "clicked");
        repository.toggleMainServiceOnOff();
    }

    public void toggleMainServiceOnOffSetting(boolean a) {

        repository.toggleMainServiceOnOff(a);
    }


}
