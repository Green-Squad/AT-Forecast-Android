package com.greensquad.atforecast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShelterActivity extends AppCompatActivity {

    static final String LOG_TAG = ShelterActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);

        final TextView shelterMile = (TextView) findViewById(R.id.shelter_mileage);
        final TextView shelterElevation = (TextView) findViewById(R.id.shelter_elevation);
        final ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.shelter_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Bundle extras = getIntent().getExtras();
        final int shelterId = extras.getInt("id");

        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);

        Call<Shelter> call = apiService.getShelter(shelterId);
        call.enqueue(new Callback<Shelter>() {
            @Override
            public void onResponse(Call<Shelter> call, Response<Shelter> response) {
                Shelter shelter = response.body();
                getSupportActionBar().setTitle(shelter.getName());

                shelterMile.setText(shelter.getMileage().toString());
                shelterElevation.setText(shelter.getElevation().toString());

                for (DailyWeather dailyWeather : shelter.getDailyWeather()) {
                    dailyWeathers.add(dailyWeather);
                }
                mAdapter = new DailyWeatherAdapter(dailyWeathers);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<Shelter>call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

}
