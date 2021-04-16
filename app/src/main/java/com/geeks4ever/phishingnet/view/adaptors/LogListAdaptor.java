package com.geeks4ever.phishingnet.view.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.URLmodel;

public class LogListAdaptor extends RecyclerView.Adapter<LogListAdaptor.ViewHolder> {

    private CircularArray<URLmodel> UrlDetails;
    private Context context;

    public LogListAdaptor(Context context){
        this.context = context;

    }

    public void updateList(CircularArray<URLmodel> UrlDetails){
        this.UrlDetails = UrlDetails;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.log_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int pos = (UrlDetails.size() -1) - position;

        holder.text.setText(UrlDetails.get(pos).url);
        holder.text.setBackground((UrlDetails.get(pos).status == URLmodel.BAD_URL)?
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.bad_log_item_bg, null) :
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.good_log_item_bg, null) );

    }

    @Override
    public int getItemCount() {
        return UrlDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public FrameLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.log_item_root);
            this.text = itemView.findViewById(R.id.log_item_url_text);
        }

    }


}
