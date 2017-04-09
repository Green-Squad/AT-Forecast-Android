package com.greensquad.atforecast.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shelter {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("mileage")
    @Expose
    private Double mileage;

    @SerializedName("elevation")
    @Expose
    private Integer elevation;

    @SerializedName("daily_weather")
    @Expose
    private List<DailyWeather> dailyWeather = null;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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

}