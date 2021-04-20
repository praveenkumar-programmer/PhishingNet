package com.geeks4ever.phishingnet.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.URLmodel;
import com.geeks4ever.phishingnet.model.repository.CommonRepository;
import com.geeks4ever.phishingnet.view.adaptors.URLListAdaptor;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class FloatingWindowService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View mFloatingWarningView;

    CommonRepository repository;
    ArrayList<URLmodel> UrlDetails;
    ArrayList<String> UrlList;
    URLListAdaptor adaptor;
    LinearLayoutManager layoutManager;

    RecyclerView recyclerView;
    TextView urlText;
    FrameLayout warningWindow, alwaysOnFloatingWindow;
    MaterialButton closeButton;

    volatile boolean isMainServiceOn = false;
    String currentUrl = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UrlDetails = new ArrayList<>();
        UrlList = new ArrayList<>();
        repository = CommonRepository.getInstance(getApplication());

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        mFloatingWarningView = LayoutInflater.from(this).inflate(R.layout.layout_floating_warning_widget, null);


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //setting the layout parameters
        final WindowManager.LayoutParams nonTouchableParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //setting the layout parameters
        final WindowManager.LayoutParams touchableParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, nonTouchableParams);
        mWindowManager.addView(mFloatingWarningView, touchableParams);

        recyclerView = mFloatingView.findViewById(R.id.floating_recycler_view);
        alwaysOnFloatingWindow = mFloatingView.findViewById(R.id.always_on_floating_window);


        warningWindow = mFloatingWarningView.findViewById(R.id.floating_warning_window);
        closeButton = mFloatingWarningView.findViewById(R.id.warning_page_close_button);
        urlText = mFloatingWarningView.findViewById(R.id.warning_page_url_text);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(warningWindow.getVisibility() == View.VISIBLE)
                    warningWindow.setVisibility(View.GONE);
            }
        });


        layoutManager = new LinearLayoutManager(this);
        adaptor = new URLListAdaptor(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFloatingWarningView.findViewById(R.id.warning_page_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(warningWindow.getVisibility() != View.GONE)
                    warningWindow.setVisibility(View.GONE);
            }
        });

        repository.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                isMainServiceOn = aBoolean;
                if(aBoolean)
                    alwaysOnFloatingWindow.setAlpha(0.5f);
                else{
                    alwaysOnFloatingWindow.setAlpha(0.0f);
                    stopSelf();
                }
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if(isMainServiceOn) {
                    if (aBoolean)
                        alwaysOnFloatingWindow.setAlpha(0.5f);
                    else
                        alwaysOnFloatingWindow.setAlpha(0.0f);
                }
                else
                    stopSelf();
            }
        });

        repository.getURLList().observeForever(new Observer<List<URLDBModel>>() {
            @Override
            public void onChanged(List<URLDBModel> urldbModels) {

                if(UrlDetails.size() > 5 && UrlList.size() > 5) {
                    UrlDetails.remove(0);
                    UrlList.remove(0);
                }
                UrlDetails.add(urldbModels.get(0).URLList.getFirst());
                UrlList.add(urldbModels.get(0).URLList.getFirst().url);

                adaptor.updateList(UrlList, UrlDetails);

                if(!currentUrl.equals(urldbModels.get(0).URLList.getFirst().url)){

                    currentUrl = urldbModels.get(0).URLList.getFirst().url;
                    if(urldbModels.get(0).URLList.getFirst().status == URLmodel.BAD_URL){

                        if(warningWindow.getVisibility() != View.VISIBLE)
                            warningWindow.setVisibility(View.VISIBLE);
                        urlText.setText(currentUrl);
                    }

                }

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }


}