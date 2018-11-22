package com.greensquad.atforecast.models;

public class Units {

    private static int unitType;

    public Units() {}

    static int getUnitType() {
        return unitType;
    }

    public static void setUnitType(int unitType) {
        Units.unitType = unitType;
    }

}