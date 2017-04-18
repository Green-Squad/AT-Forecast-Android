package com.greensquad.atforecast.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
        return temp;
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

}