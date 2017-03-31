package com.greensquad.atforecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ATForecastAPI {

    @GET("/{id}.json")
    Call<Shelter> getShelter(@Path("id") Integer id);
}