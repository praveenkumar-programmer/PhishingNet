package com.geeks4ever.phishingnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.viewmodel.SettingsViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class SettingsPage extends AppCompatActivity {

    private SettingsViewModel viewModel;

    SwitchMaterial darkModeToggle, floatingWindowToggle;
    ConstraintLayout darkModeLayout, floatingWindowLayout, appSelectionLayout, logsLayout, aboutLayout;

    public boolean nightMode = false;

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
                .AndroidViewModelFactory(  getApplication()  )).get(SettingsViewModel.class);

        nightMode = (viewModel.getNightMode() != null && viewModel.getNightMode().getValue() != null && viewModel.getNightMode().getValue());

        darkModeToggle.setChecked(nightMode);

        viewModel.getFloatingWindowServiceOnOffSetting().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                floatingWindowToggle.setChecked(aBoolean);
            }
        });

        viewModel.getNightMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if((aBoolean != nightMode)){
                    nightMode = aBoolean;
                    darkModeToggle.setChecked(aBoolean);
                    setDefaultNightMode((aBoolean)? MODE_NIGHT_YES : MODE_NIGHT_NO);
                }
            }
        });

        darkModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setNightMode( !nightMode );
            }
        });

        darkModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setNightMode( !nightMode );
            }
        });

        floatingWindowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.toggleFloatingWindowServiceOnOffSetting(
                        viewModel.getFloatingWindowServiceOnOffSetting() != null
                                && viewModel.getFloatingWindowServiceOnOffSetting().getValue() != null
                                && viewModel.getFloatingWindowServiceOnOffSetting().getValue()
                );
            }
        });

        floatingWindowToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewModel.toggleFloatingWindowServiceOnOffSetting(
                    viewModel.getFloatingWindowServiceOnOffSetting() != null
                            && viewModel.getFloatingWindowServiceOnOffSetting().getValue() != null
                            && viewModel.getFloatingWindowServiceOnOffSetting().getValue()
            );
            }
        });

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