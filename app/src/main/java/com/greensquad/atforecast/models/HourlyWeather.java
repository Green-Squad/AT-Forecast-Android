package com.greensquad.atforecast.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greensquad.atforecast.Utils;
import com.orm.SugarRecord;

public class HourlyWeather extends SugarRecord {

    @SerializedName("hourly_weather_id")
    @Expose
    private Integer hourlyWeatherId;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("temp")
    @Expose
    private Integer temp;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("wind")
    @Expose
    private String wind;

    private int dailyWeatherId;
    private String updatedAt;

    public HourlyWeather() {}

    public Integer getHourlyWeatherId() {
        return hourlyWeatherId;
    }

    public void setHourlyWeatherId(Integer hourlyWeatherId) {
        this.hourlyWeatherId = hourlyWeatherId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTemp() {
        return Units.getUnitType() == 1 ? Utils.toCelsius(temp) : temp;
    }

    public void setTemp(Integer temp) {
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getDailyWeatherId() {
        return dailyWeatherId;
    }

    public void setDailyWeatherId(int dailyWeatherId) {
        this.dailyWeatherId = dailyWeatherId;
    }
}