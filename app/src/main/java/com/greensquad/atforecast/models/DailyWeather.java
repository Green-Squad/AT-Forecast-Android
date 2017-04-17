package com.greensquad.atforecast.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class DailyWeather extends SugarRecord {
    @SerializedName("pkey")
    private long id;
    @SerializedName("id")
    @Expose
    private Integer dailyWeatherId;
    @SerializedName("weather_date")
    @Expose
    private String weatherDate;
    @SerializedName("high")
    @Expose
    private Integer high;
    @SerializedName("low")
    @Expose
    private Integer low;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("wind")
    @Expose
    private String wind;
    @SerializedName("shelter_id")
    @Expose
    private Integer shelterId;
    @SerializedName("hourly_weather")
    @Expose
    @Ignore
    private List<HourlyWeather> hourlyWeather = null;

    public DailyWeather() {}

    public Integer getDailyWeatherId() {
        return dailyWeatherId;
    }

    public void setDailyWeatherId(Integer dailyWeatherId) {
        this.dailyWeatherId = dailyWeatherId;
    }

    public String getWeatherDate() {
        return weatherDate;
    }

    public void setWeatherDate(String weatherDate) {
        this.weatherDate = weatherDate;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
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

    public Integer getShelterId() {
        return shelterId;
    }

    public void setShelterId(Integer shelterId) {
        this.shelterId = shelterId;
    }

    public List<HourlyWeather> getHourlyWeather() {
        return hourlyWeather;
    }

    public void setHourlyWeather(List<HourlyWeather> hourlyWeather) {
        this.hourlyWeather = hourlyWeather;
    }

}