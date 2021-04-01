package com.geeks4ever.phishingnet.view;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppSelectionPage extends AppCompatActivity {

    private AppListAdaptor adaptor;
    private AppSelectionViewModel viewModel;

    private ArrayList<String> tempPackageList = new ArrayList<>();
    private ArrayList<String> appPackageList = new ArrayList<>();
    private List<String> currentAppList = new ArrayList<>();
    private ArrayList<appDetails> appDetails = new ArrayList<>();

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
                if(nDialog.isShowing() && strings.size() == 0 || strings.size() == tempPackageList.size())
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
                    viewModel.addAllApps(tempPackageList);
                else
                    viewModel.removeAllApps(tempPackageList);
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
                        ArrayList<appDetails> appdetails = getInstalledApps();


                        for (int i=0; i<appdetails.size(); i++) {
                            temp.add(appdetails.get(i).packageName);
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if((currentAppList.size() == appdetails.size()) && appdetails.size() > 0)
                                    selectAll.setChecked(true);

                                appPackageList = temp;
                                tempPackageList = temp;
                                appDetails = appdetails;

                                adaptor.updateList(appdetails, appPackageList);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals("")) {
                    tempPackageList = appPackageList;
                    adaptor.updateList(appDetails, appPackageList);
                    selectAll.setEnabled(true);
                    return false;
                }


                selectAll.setEnabled(false);

                ArrayList<appDetails> temp = new ArrayList<>();
                ArrayList<String> appnamelist = new ArrayList<>();
                for(appDetails d: appDetails){

                    if(d.appName.toLowerCase().contains(newText.toLowerCase())){
                        temp.add(d);
                        appnamelist.add(d.packageName);
                    }
                }

                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);
                recyclerView.setAdapter(adaptor);
                recyclerView.setLayoutManager(new LinearLayoutManager(AppSelectionPage.this));

                tempPackageList = appnamelist;
                adaptor.updateList(temp, appnamelist);



                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}