package com.greensquad.atforecast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIController implements Callback<Shelter> {

    static final String BASE_URL = "https://www.imjordansmith.com/";
    static final String LOG_TAG = "APIController";

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ATForecastAPI atforecastAPI = retrofit.create(ATForecastAPI.class);

        Call<Shelter> call = atforecastAPI.getShelter(3);
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<Shelter> call, Response<Shelter> response) {
        if(response.isSuccessful()) {
            Shelter shelter = response.body();
            for (DailyWeather dailyWeather : shelter.getDailyWeather()) {
                Log.v(LOG_TAG, dailyWeather.getHigh().toString());
            }
        } else {
            Log.e(LOG_TAG, response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<Shelter> call, Throwable t) {
        t.printStackTrace();
    }
}
