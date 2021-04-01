package com.geeks4ever.phishingnet.view;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppSelectionPage extends AppCompatActivity {

    private AppListAdaptor adaptor;
    private AppSelectionViewModel viewModel;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_selection_page);


        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.app_selection_page_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        viewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(  getApplication()  )).get(AppSelectionViewModel.class);


        recyclerView = findViewById(R.id.app_selection_page_recycler_view);

        adaptor = new AppListAdaptor(viewModel);


        viewModel.getAppList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                adaptor.updateCurrentApps(strings);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateAppsListInBackground();

    }

    private void updateAppsListInBackground(){

        ProgressDialog nDialog;
        nDialog = new ProgressDialog(AppSelectionPage.this);
        nDialog.setMessage("getting apps..");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler =  new Handler(Looper.getMainLooper());

        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {

                        adaptor.updateList(getInstalledApps());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}