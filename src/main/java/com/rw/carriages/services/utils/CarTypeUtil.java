package com.rw.carriages.services.utils;

public class CarTypeUtil {
    public static String transformShowType(String showType) {
        if(showType!=null && showType.equals("РИЦ")) {
            showType = "Ц";
        }
        return showType;
    }
}
