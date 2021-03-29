package com.geeks4ever.phishingnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.viewmodel.commonViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsPage extends AppCompatActivity {

    private commonViewModel viewModel;

    SwitchMaterial switchMaterial;
    FrameLayout checkboxLayout;
    LinearLayout appSelection, logs, about;

    volatile boolean darkmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchMaterial = findViewById(R.id.settings_page_dark_mode_toggle);
        checkboxLayout = findViewById(R.id.settings_page_dark_mode_toggle_layout);
        appSelection = findViewById(R.id.settings_page_app_selection_layout);
        logs = findViewById(R.id.settings_page_logs_layout);
        about = findViewById(R.id.settings_page_about_layout);

        viewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(  getApplication()  )).get(commonViewModel.class);

        viewModel.getNightMode().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if(aBoolean != null){
                    darkmode = aBoolean;
                    AppCompatDelegate.setDefaultNightMode((aBoolean)? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        switchMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setNightMode(!darkmode);
            }
        });

        checkboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setNightMode(!darkmode);
            }
        });

        appSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AppSelectionPage.class));
            }
        });

        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LogActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), About.class));
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}