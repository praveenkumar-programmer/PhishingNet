package com.geeks4ever.phishingnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.viewmodel.commonViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsPage extends AppCompatActivity {

    private commonViewModel viewModel;

    SwitchMaterial darkModeToggle, floatingWindowToggle;
    ConstraintLayout darkModeLayout, floatingWindowLayout, appSelectionLayout, logsLayout, aboutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        darkModeToggle = findViewById(R.id.settings_page_dark_mode_toggle);
        floatingWindowToggle = findViewById(R.id.settings_page_floating_window_toggle);

        darkModeLayout = findViewById(R.id.settings_page_dark_mode);
        floatingWindowLayout = findViewById(R.id.settings_page_floating_window);
        appSelectionLayout = findViewById(R.id.settings_page_app_selection);
        logsLayout = findViewById(R.id.settings_page_logs);
        aboutLayout = findViewById(R.id.settings_page_about);

        viewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(  getApplication()  )).get(commonViewModel.class);

        viewModel.getNightMode().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkModeToggle.setChecked(true);
                }
                else if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkModeToggle.setChecked(false);
                }
            }
        });


        viewModel.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                floatingWindowToggle.setChecked(aBoolean);
            }
        });

        darkModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("darkmode", "clicked");
                viewModel.setNightMode(
                        viewModel.getNightMode() != null
                                && viewModel.getNightMode().getValue() != null
                                && viewModel.getNightMode().getValue()
                );
            }
        });

//        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                viewModel.setNightMode(b);
//            }
//        });


        floatingWindowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("floating", "clicked");
                viewModel.toggleFloatingWindowServiceOnOffSetting(
                        viewModel.getFloatingWindowServiceOnOffSetting() != null
                                && viewModel.getFloatingWindowServiceOnOffSetting().getValue() != null
                                && viewModel.getFloatingWindowServiceOnOffSetting().getValue()
                );
            }
        });


//        floatingWindowToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                viewModel.toggleFloatingWindowServiceOnOffSetting(b);
//            }
//        });

        appSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AppSelectionPage.class));
            }
        });

        logsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LogActivity.class));
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), About.class));
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}