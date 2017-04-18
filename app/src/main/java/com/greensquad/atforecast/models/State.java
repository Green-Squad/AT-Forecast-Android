package com.greensquad.atforecast.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.Date;
import java.util.List;

public class State extends SugarRecord implements Parcelable {

    @SerializedName("state_id")
    @Expose
    @Unique
    private Integer stateId;

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
    private Date updatedAt;

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

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

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

    public List<Shelter> getSheltersFromDb() {
        return Shelter.find(Shelter.class, "state_id = ?", getStateId() + "");
    }

    public void setShelters(List<Shelter> shelters) {
        this.shelters = shelters;
    }

    @Override
    public long save() {
        setUpdatedAt(new Date());
        return super.save();
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return  updatedAt;
    }


}