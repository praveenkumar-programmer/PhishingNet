package com.geeks4ever.phishingnet.view;

import android.content.pm.PackageInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.view.adaptors.AppListAdaptor;
import com.geeks4ever.phishingnet.viewmodel.commonViewModel;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionPage extends AppCompatActivity {

    private AppListAdaptor adaptor;
    private commonViewModel viewModel;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_selection_page);


        viewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(  getApplication()  )).get(commonViewModel.class);


        recyclerView = findViewById(R.id.app_selection_page_recycler_view);

        adaptor = new AppListAdaptor(this, viewModel);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor.updateList(getInstalledApps(false));

    }

//    class PInfo {
//        private String appname = "";
//        private String pname = "";
//        private String versionName = "";
//        private int versionCode = 0;
//        private Drawable icon;
//        private void prettyPrint() {
//            Log.e("app",appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
//        }
//    }

//    private ArrayList<PInfo> getPackages() {
//        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
//        final int max = apps.size();
//        for (int i=0; i<max; i++) {
//            apps.get(i).prettyPrint();
//        }
//        return apps;
//    }

    private ArrayList<String> getInstalledApps(boolean getSysPackages) {
        ArrayList<String> res = new ArrayList<>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
//            PInfo newInfo = new PInfo();
//            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
//            newInfo.pname = p.packageName;
//            newInfo.versionName = p.versionName;
//            newInfo.versionCode = p.versionCode;
//            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(p.packageName);
        }
        return res;
    }
}