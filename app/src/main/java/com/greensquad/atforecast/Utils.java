package com.greensquad.atforecast;

public final class Utils {
    private Utils() {}

    public static int toCelsius(int temp){
        return ((temp - 32) * 5) / 9;

    }
}
