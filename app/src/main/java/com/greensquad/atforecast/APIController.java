package com.greensquad.atforecast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.Shelter;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIController implements Callback<Shelter> {

    static final String BASE_URL = "https://dev.atforecast.com/";
    static final String LOG_TAG = "APIController";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
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
