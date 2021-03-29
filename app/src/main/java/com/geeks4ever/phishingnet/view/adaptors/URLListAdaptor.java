package com.geeks4ever.phishingnet.view.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;

import java.util.ArrayList;

public class URLListAdaptor extends RecyclerView.Adapter<URLListAdaptor.ViewHolder> {

    private ArrayList<String> list;
    private Context context;

    public URLListAdaptor(Context context){
        this.list = new ArrayList<>();
        this.context = context;

    }

    public void updateList(ArrayList<String> list){
        this.list = list;
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

        holder.text.setText(list.get(position));

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
