package com.geeks4ever.phishingnet.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
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
                currentURL = strings.get(0);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if (!isOn)
            return;

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null)
            return;

        if (AccessibilityEvent
                .eventTypeToString(accessibilityEvent.getEventType())
                .contains("TYPE_WINDOW_CONTENT_CHANGED")){

            checkUrlsFromViews(source);
        }


    }



    private void checkUrlsFromViews(AccessibilityNodeInfo source) {

        startService(new Intent(this, CheckerService.class));
        startService(new Intent(this, FloatingWindowService.class));

        if ( source != null && AppList.contains(String.valueOf(source.getPackageName()))) {

            if (source.getText() != null && source.getText().length() > 0) {

                String capturedText = source.getText().toString();

                if (!capturedText.equals(currentURL))
                    if( capturedText.contains("https://")
                            || capturedText.contains("http://") || capturedText.contains("www.")) {
                        repository.setCurrentUrl(capturedText);

                    }
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


    @Override
    public void onInterrupt() {

    }

}
