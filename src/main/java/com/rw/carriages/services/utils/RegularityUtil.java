package com.rw.carriages.services.utils;

import java.util.Calendar;
import java.util.Date;

public class RegularityUtil {
    public static String getDayWeekNum(Date selDate) {
        Calendar cal = dateToCalendar(selDate);
        int day = cal.get(Calendar.DAY_OF_WEEK)-1;
        if(day==0) {
            day = 7;
        }
        return String.valueOf(day);
    }

    public static String getDayParity(Date selDate) {
        Calendar cal = dateToCalendar(selDate);
        int num = cal.get(Calendar.DAY_OF_MONTH)%2;
        return num==1?"н":"ч";
    }

    public static Calendar dateToCalendar(Date selDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selDate);
        return cal;
    }
}
