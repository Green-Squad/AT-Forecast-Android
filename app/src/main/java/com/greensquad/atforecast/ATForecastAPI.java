package com.greensquad.atforecast;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ATForecastAPI {

    @GET("/index.json")
    Call<List<State>> getStates();

    @GET("/shelters/{id}.json")
    Call<Shelter> getShelter(@Path("id") Integer id);
}