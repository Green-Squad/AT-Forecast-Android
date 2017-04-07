package com.greensquad.atforecast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SheltersActivity extends AppCompatActivity {

    static final String LOG_TAG = SheltersActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelters);

        recyclerView = (RecyclerView) findViewById(R.id.shelters_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Bundle extras = getIntent().getExtras();
        //int stateId = extras.getInt("id");
        String sheltersString = extras.getString("shelters");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Shelter>>() {}.getType();
        List<Shelter> sheltersList = gson.fromJson(sheltersString, listType);
        ArrayList<Shelter> sheltersArrayList = new ArrayList<Shelter>(sheltersList);

        String stateName = extras.getString("stateName");
        getSupportActionBar().setTitle(stateName);

        mAdapter = new SheltersAdapter(sheltersArrayList);
        recyclerView.setAdapter(mAdapter);
    }
}
