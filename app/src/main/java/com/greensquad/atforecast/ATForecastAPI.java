package com.greensquad.atforecast;

import com.greensquad.atforecast.models.Shelter;
import com.greensquad.atforecast.models.State;

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