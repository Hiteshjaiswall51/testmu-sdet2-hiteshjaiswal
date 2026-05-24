package com.testmu.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtility {
    public static String currentDateAndTime(String format) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        String date = now.format(dtf);
        return date;
    }

    public static String addedMinutesTimes(String format, int n) {
        LocalDateTime now = LocalDateTime.now().plusMinutes(n);  // Add n minutes to current time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        String date = now.format(dtf);
        return date;
    }
}
