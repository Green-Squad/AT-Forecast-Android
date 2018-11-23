package com.greensquad.atforecast;

public final class Utils {
    private Utils() {}

    public static int toCelsius(int temp){
        double newTemp = ((temp - 32) * 5.0) / 9.0;
        return (int) Math.round(newTemp);
    }
}
