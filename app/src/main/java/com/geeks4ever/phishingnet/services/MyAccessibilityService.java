package com.geeks4ever.phishingnet.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

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

    @Override
    public void onCreate() {
        super.onCreate();
        repository = CommonRepository.getInstance(getApplication());
        AppList = new ArrayList<>();

        startService(new Intent(getBaseContext(), FloatingWindowService.class));

        repository.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings == null || strings.isEmpty())
                    AppList = new ArrayList<>();
                else
                    AppList = strings;
            }
        });

        repository.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    isOn = aBoolean;
                else
                    isOn = false;
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    isFloatingWindowOn = aBoolean;
                else
                    isFloatingWindowOn = false;
            }
        });

        repository.getCurrentUrl().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (strings == null || strings.isEmpty())
                    currentURL = "";
                else
                    currentURL = strings.get(0);
            }
        });

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if (!isOn)
            return;

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null)
            return;

        if (AppList.contains(String.valueOf(source.getPackageName()))) {

            if (source.getText() != null && source.getText().length() > 0) {

                String capturedText = source.getText().toString();

                Log.e("kpk", capturedText);
                if (!capturedText.equals(currentURL))
                    if( capturedText.contains("https://")
                        || capturedText.contains("http://") || capturedText.contains("www.")) {
                        repository.setCurrentUrl(capturedText);
//                    if(isFloatingWindowOn)
//                        startService(new Intent(this, CheckerService.class));

                }

//                for (int i = 0; i < info.getChildCount(); i++) {
//                    AccessibilityNodeInfo child = info.getChild(i);
//                    getUrlsFromViews(child);
//                    if (child != null) {
//                        child.recycle();
//                    }
//                }

            }
        }

    }


    @Override
    public void onInterrupt() {

    }

}
