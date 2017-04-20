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

    @SerializedName("latt")
    @Expose
    private Double latitude;

    @SerializedName("long")
    @Expose
    private Double longitude;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public Integer findByNearestCoords(double latitude, double longitude) {
        double smallestDistance = Double.MAX_VALUE;
        Shelter smallestShelter = null;

        List<Shelter> shelters = Shelter.listAll(Shelter.class);
        for (Shelter shelter : shelters) {
            double distance = haversine(
                    new double[]{latitude, longitude},
                    new double[]{shelter.getLatitude(), shelter.getLongitude()}
            );

            if(distance < smallestDistance) {
                smallestDistance = distance;
                smallestShelter = shelter;
            }
        }

        return smallestShelter.getShelterId();
    }

    // Use Latitude and Longitude coordinates
    public static double haversine(double[] start, double[] end) {
        final double R = 6372.8; // In kilometers
        double lat1 = start[0];
        double lat2 = end[0];
        double lon1 = start[1];
        double lon2 = end[1];

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

}