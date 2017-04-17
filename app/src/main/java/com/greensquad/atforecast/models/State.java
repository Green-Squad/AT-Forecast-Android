package com.greensquad.atforecast.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class State extends SugarRecord implements Parcelable {
    @SerializedName("pkey")
    private long id;
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
    @Ignore
    private List<Shelter> shelters = null;

    @Override
    public int describeContents() {
        return 0;
    }

    public State() {}

    protected State(Parcel in) {
        name = in.readString();
        averageHigh = in.readInt();
        averageLow = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(averageHigh);
        dest.writeInt(averageLow);
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };

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