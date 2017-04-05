package com.greensquad.atforecast;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class State {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("average_high")
    @Expose
    private Integer averageHigh;
    @SerializedName("average_low")
    @Expose
    private Integer averageLow;
    @SerializedName("shelters")
    @Expose
    private List<Shelter> shelters = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAverageHigh() {
        return averageHigh;
    }

    public void setAverageHigh(Integer averageHigh) {
        this.averageHigh = averageHigh;
    }

    public Integer getAverageLow() {
        return averageLow;
    }

    public void setAverageLow(Integer averageLow) {
        this.averageLow = averageLow;
    }

    public List<Shelter> getShelters() {
        return shelters;
    }

    public void setShelters(List<Shelter> shelters) {
        this.shelters = shelters;
    }

}