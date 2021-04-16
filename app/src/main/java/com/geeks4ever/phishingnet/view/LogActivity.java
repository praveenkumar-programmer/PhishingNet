package com.geeks4ever.phishingnet.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geeks4ever.phishingnet.R;
import com.geeks4ever.phishingnet.model.URLDBModel;
import com.geeks4ever.phishingnet.model.repository.CommonRepository;
import com.geeks4ever.phishingnet.view.adaptors.LogListAdaptor;

import java.util.List;

public class LogActivity extends AppCompatActivity {


    CommonRepository repository;
    LogListAdaptor adaptor;
    LinearLayoutManager layoutManager;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_page);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Logs");
        }


        recyclerView = findViewById(R.id.log_page_recycler_view);

        repository = CommonRepository.getInstance(getApplication());


        layoutManager = new LinearLayoutManager(this);
        adaptor = new LogListAdaptor(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository.getURLList().observeForever(new Observer<List<URLDBModel>>() {
            @Override
            public void onChanged(List<URLDBModel> urldbModels) {
                if(urldbModels != null && !urldbModels.isEmpty())
                    adaptor.updateList(urldbModels.get(0).URLList);
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}