package com.rw.carriages.services.utils;

public class StationUtil {
    public static final String TICKET_NATIONAL_CARRIER_COUNTRY = "21";
    public static boolean isBelarusStation(String code) {
        if(code.startsWith(TICKET_NATIONAL_CARRIER_COUNTRY)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isBelarusStations(String code1, String code2) {
        if(code1.startsWith(TICKET_NATIONAL_CARRIER_COUNTRY) && code2.startsWith(TICKET_NATIONAL_CARRIER_COUNTRY)) {
            return true;
        } else {
            return false;
        }
    }

}
