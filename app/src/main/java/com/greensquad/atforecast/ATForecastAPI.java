package com.greensquad.atforecast;

import com.greensquad.atforecast.models.Shelter;
import com.greensquad.atforecast.models.State;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ATForecastAPI {

    @GET("/index.json")
    Call<List<State>> getStates(@Query("include_shelters") boolean includeShelters, @Query("api_key") String apiKey);

    @GET("/shelters/{id}.json")
    Call<List<Shelter>> getShelter(@Path("id") Integer id, @Query("api_key") String apiKey, @Query("dist_miles") Integer distMiles);
}