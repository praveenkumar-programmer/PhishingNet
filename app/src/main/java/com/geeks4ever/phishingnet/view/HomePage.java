package com.geeks4ever.phishingnet.view;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.services.FloatingWindowService;
import com.geeks4ever.phishingnet.services.MyAccessibilityService;
import com.geeks4ever.phishingnet.viewmodel.commonViewModel;
import com.google.android.material.button.MaterialButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class HomePage extends AppCompatActivity {

    private commonViewModel viewModel;

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
                .AndroidViewModelFactory(  getApplication()  )).get(commonViewModel.class);


        getPermissions();

        viewModel.getFloatingWindowServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null && aBoolean)
                    startService(new Intent(getBaseContext(), FloatingWindowService.class));
            }
        });

        viewModel.getMainServiceOnOffSetting().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                Log.e("toggle", String.valueOf(aBoolean));

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

        viewModel.getNightMode().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                AppCompatDelegate.setDefaultNightMode((aBoolean)? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.toggleMainServiceOnOffSetting();
            }
        });

    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }


    private void getPermissions() {


        if(!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            viewModel.toggleMainServiceOnOffSetting(false);
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, 0);
        }

        if( !(Settings.canDrawOverlays(this)))
            Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.SYSTEM_ALERT_WINDOW)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }
//                        else{
//                            tryAgain();
//                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void tryAgain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs OVERLAY permissions to work.");
        builder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermissions();
            }
        });
        builder.setNegativeButton("CLOSE APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs OVERLAY permissions to work. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("CLOSE APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();

    }


    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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


}