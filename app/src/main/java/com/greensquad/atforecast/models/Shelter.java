package com.greensquad.atforecast.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

public class Shelter extends SugarRecord {
    @SerializedName("pkey")
    private long id;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private Integer shelterId;

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

}