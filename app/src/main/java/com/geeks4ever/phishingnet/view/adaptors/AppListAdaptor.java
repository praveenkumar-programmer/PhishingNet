package com.geeks4ever.phishingnet.view.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.viewmodel.commonViewModel;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class AppListAdaptor extends RecyclerView.Adapter<AppListAdaptor.ViewHolder> {

    private ArrayList<String> allAppsList;
    private List<String> currentAppsList;
    private Context context;
    private commonViewModel viewModel;

    public AppListAdaptor(Context context, commonViewModel viewModel){
        this.allAppsList = new ArrayList<>();
        this.context = context;
        this.viewModel = viewModel;

        allAppsList = new ArrayList<>();
        currentAppsList = new ArrayList<>();

        viewModel.getAppList().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                currentAppsList = strings;
            }
        });

    }

    public void updateList(ArrayList<String> list){
        this.allAppsList = list;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.app_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final MaterialCheckBox checkbox = holder.checkBox;

        if(currentAppsList.contains(allAppsList.get(position))) holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);

        holder.appPackageName.setText(allAppsList.get(position));
//        holder.checkNoxLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isPresent){
//                    checkbox.setChecked(false);
//                    Log.e("removed", allAppsList.get(position));
//                    viewModel.removeApp(allAppsList.get(position));
//                }
//                else{
//                    checkbox.setChecked(true);
//                    Log.e("added", allAppsList.get(position));
//                    viewModel.addApp(allAppsList.get(position));
//                }
//            }
//        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentAppsList.contains(allAppsList.get(position))){
                    checkbox.setChecked(false);
                    Log.e("removed", allAppsList.get(position));
                    viewModel.removeApp(allAppsList.get(position));
                }
                else{
                    checkbox.setChecked(true);
                    Log.e("added", allAppsList.get(position));
                    viewModel.addApp(allAppsList.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return allAppsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView appName, appPackageName;
        public ImageView appIcon;
        public MaterialCheckBox checkBox;
        public FrameLayout checkNoxLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.appName = itemView.findViewById(R.id.app_list_item_app_name);
            this.appPackageName = itemView.findViewById(R.id.app_list_item_app_package_name);
            this.appIcon = itemView.findViewById(R.id.app_list_item_app_icon);
            this.checkBox = itemView.findViewById(R.id.app_list_item_app_check_box);
            this.checkNoxLayout = itemView.findViewById(R.id.app_list_item_app_check_box_layout);

        }

    }


}
