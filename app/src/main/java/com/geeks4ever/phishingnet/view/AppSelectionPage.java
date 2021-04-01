package com.geeks4ever.phishingnet.view;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.appDetails;
import com.geeks4ever.phishingnet.view.adaptors.AppListAdaptor;
import com.geeks4ever.phishingnet.viewmodel.AppSelectionViewModel;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AppSelectionPage extends AppCompatActivity {

    private AppListAdaptor adaptor;
    private AppSelectionViewModel viewModel;

    private ArrayList<String> appPackageList = new ArrayList<>();
    private List<String> currentAppList = new ArrayList<>();

    RecyclerView recyclerView;
    MaterialCheckBox selectAll;
    TextView selectAllText;
    ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_selection_page);

        nDialog = new ProgressDialog(AppSelectionPage.this);
        nDialog.setMessage("getting apps..");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);


        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.app_selection_page_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        selectAll = findViewById(R.id.app_selection_page_select_all_check_box);
        selectAllText = findViewById(R.id.app_selection_page_select_all_text);


        viewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(  getApplication()  )).get(AppSelectionViewModel.class);


        recyclerView = findViewById(R.id.app_selection_page_recycler_view);

        adaptor = new AppListAdaptor(viewModel);

        getAppDetailsInBackground();

        viewModel.getAppList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                currentAppList = strings;
                selectAllText.setText(strings.size() + " Apps Selected");
                adaptor.updateCurrentApps(strings);
                if(nDialog.isShowing() && strings.size() == 0 || strings.size() == appPackageList.size())
                    nDialog.dismiss();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    viewModel.addAllApps(appPackageList);
                else
                    viewModel.removeAllApps(appPackageList);
                nDialog.show();
                adaptor.notifyDataSetChanged();
            }
        });


    }

    private void getAppDetailsInBackground() {

        nDialog.show();

        Executors.newSingleThreadExecutor()
                .execute(
                new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<String> temp = new ArrayList<>();
                        ArrayList<appDetails> appDetails = getInstalledApps();

                        for (int i=0; i<appDetails.size(); i++) {
                            temp.add(appDetails.get(i).packageName);
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if((currentAppList.size() == appDetails.size()) && appDetails.size() > 0)
                                    selectAll.setChecked(true);

                                appPackageList = temp;

                                Log.e("lis size = ", String.valueOf(appPackageList.size()));
                                Log.e("applist size", String.valueOf(appDetails.size()));

                                adaptor.updateList(appDetails, appPackageList);
                                adaptor.notifyDataSetChanged();
                                nDialog.dismiss();
                            }
                        });
                    }
                }
        );
    }


    private ArrayList<appDetails> getInstalledApps() {

        ArrayList<appDetails> res = new ArrayList<>();

        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((p.versionName == null)) {
                continue ;
            }
            appDetails newInfo = new appDetails();
            newInfo.appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.packageName = p.packageName;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(newInfo);
        }

        return res;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getAppDetailsInBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppDetailsInBackground();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}