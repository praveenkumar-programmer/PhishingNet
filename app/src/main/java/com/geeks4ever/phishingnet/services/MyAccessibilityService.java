package com.geeks4ever.phishingnet.services;

import android.accessibilityservice.AccessibilityService;
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

        repository.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                AppList = strings;
            }
        });

        repository.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isOn = aBoolean;
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isFloatingWindowOn = aBoolean;
            }
        });

        repository.getCurrentUrl().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings == null || strings.isEmpty())
                    currentURL = "";
                else
                    currentURL = strings.get(0);
            }
        });

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if( !isOn)
            return;

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null)
            return;

        if(AppList.contains(String.valueOf(source.getPackageName()))){

            if (source.getText() != null && source.getText().length() > 0) {

                String capturedText = source.getText().toString();

                Log.e("kpk", capturedText);
                if (!capturedText.equals(currentURL) || capturedText.contains("https://")
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
