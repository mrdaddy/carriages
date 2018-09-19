package com.rw.carriages.services.utils;

public class CoordinateCalculator {
    public static int calculate(int val, int widthOrig, int width) {
        if(widthOrig != width) {
            float diff = (float)widthOrig/(float)width;
            val = (int)((float)val/diff+0.5);
        }
        return val;
    }

}
