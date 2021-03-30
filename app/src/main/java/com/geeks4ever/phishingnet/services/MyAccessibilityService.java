package com.geeks4ever.phishingnet.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.geeks4ever.phishingnet.model.repository.CommonRepository;

import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    CommonRepository repository;
    List<String> AppList;
    String currentURL;
    volatile boolean isOn;
    volatile boolean isFloatingWindowOn;


    private LiveData<List<String>> AppListLiveData;
    private LiveData<Boolean> MainServiceOnOffSettingLiveData;
    private LiveData<Boolean> FloatingServiceOnOffSettingLiveData;
    private LiveData<List<String>> CurrentURLLiveData;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = CommonRepository.getInstance(getApplication());
        AppList = new ArrayList<>();


        Log.e("inside accessibility", "created");

        AppListLiveData = repository.getAppList();

        AppListLiveData.observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                AppList = strings;
            }
        });

        MainServiceOnOffSettingLiveData = repository.getMainServiceOnOffSetting();

        MainServiceOnOffSettingLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.e("inside acc serv", String.valueOf(aBoolean));
                isOn = aBoolean;
            }
        });

        FloatingServiceOnOffSettingLiveData = repository.getFloatingWindowServiceOnOffSetting();
        FloatingServiceOnOffSettingLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isFloatingWindowOn = aBoolean;
            }
        });

        CurrentURLLiveData = repository.getCurrentUrl();
        CurrentURLLiveData.observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                currentURL = strings.get(0);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("inside accessibility", "onStart");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.e("inside accessibility", "accessibility event");

        if (!isOn)
            return;

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null)
            return;

        checkUrlsFromViews(source);

    }



    private void checkUrlsFromViews(AccessibilityNodeInfo source) {

        if (AppList.contains(String.valueOf(source.getPackageName()))) {

            if (source.getText() != null && source.getText().length() > 0) {

                String capturedText = source.getText().toString();

                Log.e("captured link", capturedText);

                if (!capturedText.equals(currentURL))
                    if( capturedText.contains("https://")
                            || capturedText.contains("http://") || capturedText.contains("www.")) {
                        repository.setCurrentUrl(capturedText);
                        startService(new Intent(this, CheckerService.class));
                        if(isFloatingWindowOn)
                            startService(new Intent(getBaseContext(), FloatingWindowService.class));

                    }

                for (int i = 0; i < source.getChildCount(); i++) {
                    AccessibilityNodeInfo child = source.getChild(i);
                    checkUrlsFromViews(child);
                    if (child != null) {
                        child.recycle();
                    }
                }

            }
        }

    }


    @Override
    public void onInterrupt() {

    }

}
