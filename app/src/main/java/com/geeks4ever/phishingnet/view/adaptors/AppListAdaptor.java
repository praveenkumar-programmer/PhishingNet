package com.geeks4ever.phishingnet.view.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.appDetails;
import com.geeks4ever.phishingnet.viewmodel.AppSelectionViewModel;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class AppListAdaptor extends RecyclerView.Adapter<AppListAdaptor.ViewHolder> {

    private ArrayList<String> allAppsList;
    private volatile List<String> currentAppsList;

    private ArrayList<appDetails> appDetails;

    private final AppSelectionViewModel viewModel;

    public AppListAdaptor(AppSelectionViewModel viewModel){
        this.allAppsList = new ArrayList<>();
        this.viewModel = viewModel;

        allAppsList = new ArrayList<>();
        currentAppsList = new ArrayList<>();

        appDetails = new ArrayList<>();

    }

    public void updateList(ArrayList<appDetails> list, ArrayList<String> appNameList){

        this.appDetails = list;
        allAppsList = appNameList;
        if(viewModel.getAppList() != null && viewModel.getAppList().getValue() != null )
            currentAppsList = viewModel.getAppList().getValue();
    }

    public void updateCurrentApps(List<String> currentList){
        this.currentAppsList = currentList;
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

        holder.checkBox.setChecked(currentAppsList.contains(allAppsList.get(position)));

        holder.appIcon.setImageDrawable(appDetails.get(position).icon);
        holder.appName.setText(appDetails.get(position).appName);
        holder.appPackageName.setText(allAppsList.get(position));


        holder.app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentAppsList.contains(allAppsList.get(position))){
                    holder.checkBox.setChecked(false);
                    viewModel.removeApp(allAppsList.get(position));
                }
                else{
                    holder.checkBox.setChecked(true);
                    viewModel.addApp(allAppsList.get(position));
                }
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentAppsList.contains(allAppsList.get(position))){
                    holder.checkBox.setChecked(false);
                    viewModel.removeApp(allAppsList.get(position));
                }
                else{
                    holder.checkBox.setChecked(true);
                    viewModel.addApp(allAppsList.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return appDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView appName, appPackageName;
        public ImageView appIcon;
        public MaterialCheckBox checkBox;
        public ConstraintLayout app;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.appName = itemView.findViewById(R.id.app_list_item_app_name);
            this.appPackageName = itemView.findViewById(R.id.app_list_item_app_package_name);
            this.appIcon = itemView.findViewById(R.id.app_list_item_app_icon);
            this.checkBox = itemView.findViewById(R.id.app_list_item_app_check_box);
            this.app = itemView.findViewById(R.id.app_item);

        }

    }


}
