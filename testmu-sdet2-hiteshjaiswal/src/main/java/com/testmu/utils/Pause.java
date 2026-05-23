package com.testmu.utils;

import java.time.Duration;

public class Pause {
    public static String V_SMALL="3";
    public static String SMALL="5";
    public static String LOW="15";
    public static String MEDIUM="30";
    public static String HIGH="60";
    public static String V_HIGH="100";


    public static void sleep (long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static Duration duration(String seconds) {
        return Duration.ofSeconds(Long.parseLong(seconds));
    }
}
