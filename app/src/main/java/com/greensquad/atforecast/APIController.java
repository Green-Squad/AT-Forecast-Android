package com.greensquad.atforecast;

import android.util.Log;

import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.Shelter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIController implements Callback<Shelter> {

    private static final String BASE_URL = "https://www.atforecast.com/";
    private static final String LOG_TAG = "APIController";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
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
