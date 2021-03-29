package com.geeks4ever.phishingnet.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.repository.CommonRepository;
import com.geeks4ever.phishingnet.view.adaptors.URLListAdaptor;

import java.util.ArrayList;
import java.util.List;

public class FloatingWindowService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View mFloatingWarningView;

    CommonRepository repository;
    ArrayList<String> URLList;
    URLListAdaptor adaptor;

    RecyclerView recyclerView;
    TextView urlText;
    FrameLayout warningWindow, alwaysOnFloatingWindow;

    volatile boolean isMainServiceOn = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("kpk", "inside floating service");

        URLList = new ArrayList<>();
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
        //mWindowManager.addView(mFloatingWarningView, touchableParams);

        recyclerView = mFloatingView.findViewById(R.id.floating_recycler_view);
        warningWindow = mFloatingWarningView.findViewById(R.id.floating_warning_window);
        urlText = mFloatingWarningView.findViewById(R.id.warning_page_url_text);
        alwaysOnFloatingWindow = mFloatingView.findViewById(R.id.always_on_floating_window);


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
                else
                    alwaysOnFloatingWindow.setAlpha(0.0f);
            }
        });

        repository.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(isMainServiceOn && aBoolean)
                            alwaysOnFloatingWindow.setAlpha(0.5f);
                else
                    stopSelf();
            }
        });

        repository.getCurrentUrl().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null && !strings.isEmpty() && !strings.get(0).isEmpty()){

                    if(URLList.size() > 5)
                        URLList.remove(0);
                    URLList.add(strings.get(0));

                    adaptor.updateList(URLList);
                    recyclerView.smoothScrollToPosition(URLList.size());

//                    if(urldbModels.get(0).URLList.getFirst().status == URLmodel.BAD_URL){
//                        if(warningWindow.getVisibility() != View.VISIBLE)
//                            warningWindow.setVisibility(View.VISIBLE);
//                        urlText.setText(urldbModels.get(0).URLList.getFirst().url);
//                    }

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