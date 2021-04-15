package com.geeks4ever.phishingnet.view.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.URLmodel;

import java.util.ArrayList;

public class URLListAdaptor extends RecyclerView.Adapter<URLListAdaptor.ViewHolder> {

    private ArrayList<String> list;
    private ArrayList<URLmodel> UrlDetails;
    private Context context;

    public URLListAdaptor(Context context){
        this.context = context;
        this.list = new ArrayList<>();

    }

    public void updateList(ArrayList<String> list, ArrayList<URLmodel> UrlDetails){
        this.list = list;
        this.UrlDetails = UrlDetails;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.url_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int pos = (list.size() -1) - position;

        holder.text.setText(UrlDetails.get(pos).url);
        holder.text.setTextColor((UrlDetails.get(pos).status == URLmodel.BAD_URL)?
                context.getResources().getColor(R.color.warning_red) :
                context.getResources().getColor(R.color.tech_blue));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.text_in_recycler_tem);
        }

    }


}
