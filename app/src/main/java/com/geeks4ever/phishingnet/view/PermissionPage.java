package com.geeks4ever.phishingnet.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.geeks4ever.phishingnet.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class PermissionPage extends AppCompatActivity {

    MaterialTextView overlayNumber, overlayTitle, overlayDescription,
            accessibilityNumber, accessibilityTitle, accessibilityDescription;

    MaterialButton overlayButton, accessibilityButton, finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_page);

        overlayNumber = findViewById(R.id.overlay_permission_number);
        overlayTitle = findViewById(R.id.overlay_permission_heading);
        overlayDescription = findViewById(R.id.overlay_permission_description);
        overlayButton = findViewById(R.id.overlay_permission_button);

        accessibilityNumber = findViewById(R.id.accessibility_permission_number);
        accessibilityTitle = findViewById(R.id.accessibility_permission_heading);
        accessibilityDescription = findViewById(R.id.accessibility_permission_description);
        accessibilityButton = findViewById(R.id.accessibility_permission_button);

        finish = findViewById(R.id.finish);

        overlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOverlayPermission();
            }
        });

        accessibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAccessibilityPermission();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndFinishIfAllPermissionsGranted();
            }
        });

        setPermissionRequirements();

    }

    private void checkAndFinishIfAllPermissionsGranted() {

        if(!(Settings.canDrawOverlays(this)))
            Toast.makeText(this, "Please allow OVERLAY permission.", Toast.LENGTH_SHORT).show();

        else if(!isAccessibilityEnabled())
            Toast.makeText(this, "Please allow ACCESSIBILITY permission.", Toast.LENGTH_SHORT).show();
        else
            finish();

    }


    private void getOverlayPermission(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getAccessibilityPermission(){
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 101);
    }

    private void setPermissionRequirements(){

        int count = 0;

        if(!(Settings.canDrawOverlays(this))){
            count++;
            setOverlayDivisionVisible(count, View.VISIBLE);
        }
        else
            setOverlayDivisionVisible(count, View.GONE);

        if(!isAccessibilityEnabled()){
            count++;
            setAccessibilityDivisionVisible(count, View.VISIBLE);
        }
        else
            setAccessibilityDivisionVisible(count, View.GONE);

        if(count == 0)
            finish();

    }


    private void setOverlayDivisionVisible(int index, int visibity){

        overlayNumber.setText(String.valueOf(index));
        if (overlayNumber.getVisibility() != visibity) overlayNumber.setVisibility(visibity);
        if(overlayTitle.getVisibility() != visibity) overlayTitle.setVisibility(visibity);
        if(overlayDescription.getVisibility() != visibity) overlayDescription.setVisibility(visibity);
        if(overlayButton.getVisibility() != visibity) overlayButton.setVisibility(visibity);

    }

    private void setAccessibilityDivisionVisible(int index, int visibity){

        accessibilityNumber.setText(String.valueOf(index));
        if (accessibilityNumber.getVisibility() != visibity) accessibilityNumber.setVisibility(visibity);
        if(accessibilityTitle.getVisibility() != visibity) accessibilityTitle.setVisibility(visibity);
        if(accessibilityDescription.getVisibility() != visibity) accessibilityDescription.setVisibility(visibity);
        if(accessibilityButton.getVisibility() != visibity) accessibilityButton.setVisibility(visibity);

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


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "This app needs the above permissions to function", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setPermissionRequirements();
    }

}