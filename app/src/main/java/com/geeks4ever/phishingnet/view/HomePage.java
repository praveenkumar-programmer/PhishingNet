package com.geeks4ever.phishingnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.viewmodel.HomePageViewModel;
import com.google.android.material.button.MaterialButton;

public class HomePage extends AppCompatActivity {

    private HomePageViewModel viewModel;

    ImageView imageView;
    TextView textView;
    MaterialButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        imageView = findViewById(R.id.home_page_image);
        textView = findViewById(R.id.home_page_status_text);
        button = findViewById(R.id.home_page_button);

        viewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(  getApplication()  )).get(HomePageViewModel.class);

        getPermissions();

        viewModel.getMainServiceOnOffSetting().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if(aBoolean){
                    textView.setText("YOU ARE PROTECTED");
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.on_icon, null));
                    button.setText("disable");
                }else{
                    textView.setText("YOU ARE NOT PROTECTED");
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.off_icon, null));
                    button.setText("enable");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.toggleMainServiceOnOffSetting();
            }
        });

    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = "com.geeks4ever.phishingnet/com.geeks4ever.phishingnet.services.MyAccessibilityService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d("LOGTAG", "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d("LOGTAG", "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {
            Log.d("LOGTAG", "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d("LOGTAG", "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    Log.d("LOGTAG", "Setting: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)){
                        Log.d("LOGTAG", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            Log.d("LOGTAG", "***END***");
        }
        else {
            Log.d("LOGTAG", "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

    private void getPermissions() {

        if(!isAccessibilityEnabled()  || !(Settings.canDrawOverlays(this))) {
            viewModel.toggleMainServiceOnOffSetting(false);
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(new Intent(this, PermissionPage.class));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsButton) {
            startActivity(new Intent(this, SettingsPage.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        getPermissions();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        getPermissions();
        super.onRestart();
    }
}