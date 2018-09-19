package com.rw.carriages.services.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConverter {
    private static final ThreadLocal<SimpleDateFormat> SHORT_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat;
        }
    };
    private static final ThreadLocal<SimpleDateFormat> FULL_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            return dateFormat;
        }
    };
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
            return dateFormat;
        }
    };
    private static final ThreadLocal<SimpleDateFormat> DATE_FULL_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return dateFormat;
        }
    };
    private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat;
        }
    };
    private static final ThreadLocal<SimpleDateFormat> YEAR_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
            return dateFormat;
        }
    };

    public static String getDateTimeString(Date date) {
        return FULL_FORMAT.get().format(date);
    }

    public static String getDateString(Date date) {
        return DATE_FORMAT.get().format(date);
    }

    public static String getDateFullString(Date date) {
        return DATE_FULL_FORMAT.get().format(date);
    }

    public static String getTimeString(Date date) {
        return TIME_FORMAT.get().format(date);
    }

    public static String getYearString(Date date) {
        return YEAR_FORMAT.get().format(date);
    }

    public static Date createDateFromString(String date, String time) throws ParseException {
        if(StringUtils.isEmpty(date)) {
            date = "01.01.2000";
        }
        String dateTimeStr = date+" "+time;
        return FULL_FORMAT.get().parse(dateTimeStr);
    }

    public static Date createDateFromString(String date) throws ParseException {
        String dateTimeStr;
        if(date.length()>"01.01.2000".length()) {
            dateTimeStr = date;
        } else {
            dateTimeStr = date+" 00:00";
        }
        return FULL_FORMAT.get().parse(dateTimeStr);
    }
}
