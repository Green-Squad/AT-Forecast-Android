package com.greensquad.atforecast.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.List;

public class Shelter extends SugarRecord {

    @SerializedName("shelter_id")
    @Expose
    @Unique
    private Integer shelterId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("mileage")
    @Expose
    private Double mileage;

    @SerializedName("elevation")
    @Expose
    private Integer elevation;

    @SerializedName("daily_weather")
    @Expose
    @Ignore
    private List<DailyWeather> dailyWeather = null;

    private int stateId;

    public Shelter() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getShelterId() {
        return shelterId;
    }
    public void setShelterId(Integer shelterId) {
        this.shelterId = shelterId;
    }

    public Double getMileage() { return mileage; }
    public void setMileage(Double mileage) { this.mileage = mileage; }

    public Integer getElevation() { return elevation; }
    public void setElevation(Integer elevation) { this.elevation = elevation; }

    public List<DailyWeather> getDailyWeather() {
        return dailyWeather;
    }
    public void setDailyWeather(List<DailyWeather> dailyWeather) {
        this.dailyWeather = dailyWeather;
    }

    public List<DailyWeather> getDailyWeatherFromDb() {
        return DailyWeather.find(DailyWeather.class, "daily_weather_id = ?", getShelterId() + "");
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

}