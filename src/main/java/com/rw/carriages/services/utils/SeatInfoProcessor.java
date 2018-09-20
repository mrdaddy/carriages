package com.rw.carriages.services.utils;

import java.util.*;

import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.SeatCoordinate;
import com.rw.carriages.dto.request.CarriageInfo;
import com.rw.carriages.services.transform.GraphicSeatBean;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StringUtils;

public class SeatInfoProcessor {
    private Map<String,Object[]> freePlacesMap = new HashMap<String,Object[]>();
    private Map<String,Double> placesAdd;
    private Set<String> places;
    private String serviceClassInt;
    private String carTypeLetterShow;
    private Double tariff;
    public SeatInfoProcessor(CarriageInfo carriageInfo, Map<String,Double> addFreePlaces) {
        String freePlacesStr = carriageInfo.getFreeSeats();
       // this.placesAdd = StringUtils.collectionToCommaDelimitedString(addFreePlaces.keySet());
        if(addFreePlaces!=null) {
            this.placesAdd = addFreePlaces;
        } else {
            this.placesAdd = new HashMap<>();
        }
        this.places = StringUtils.commaDelimitedListToSet(carriageInfo.getFreeSeats());
        this.tariff = carriageInfo.getTariff();
        this.serviceClassInt = carriageInfo.getServiceClassIntCode();
        this.carTypeLetterShow = CarTypeUtil.transformShowType(carriageInfo.getTypeCodeShow());
        //fillFaresMap(options.getFares());
        if(!StringUtils.isEmpty(freePlacesStr)) {
            parseFreePlaces(freePlacesStr);
        }
    }

    public String getCoupType(String no) {
        if(freePlacesMap.containsKey(no) && freePlacesMap.get(no)!=null) {
            return (String)freePlacesMap.get(no)[0];
        } else {
            return "";
        }
    }

    public Double getTariff(String no) {
        if(freePlacesMap.containsKey(no) && freePlacesMap.get(no).length>1 && freePlacesMap.get(no)[1]!=null) {
            return (Double)(freePlacesMap.get(no)[1]);
        } else {
            return 0d;
        }
    }

    public SeatCoordinate.ACCESS_TYPE getStyleClassByAddPlaces(String no, SeatCoordinate.ACCESS_TYPE styleClass) {
        styleClass = isInAddPlaces(no)?SeatCoordinate.ACCESS_TYPE.F:styleClass;
        return styleClass;
    }

    private boolean isInAddPlaces(String no) {
        boolean result = false;
        Set<String> places = placesAdd.keySet();
        if(places!=null) {
            for(String place: places) {
                if(parseNo(place)==parseNo(no)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public SeatCoordinate.ACCESS_TYPE getSingleDoubleStyleClass(String no, SeatCoordinate.ACCESS_TYPE styleClass, String seatType,int coupeNo, JSONArray signArray) throws JSONException {

        if(serviceClassInt.equals("1/2") || serviceClassInt.equals("1/1")) {
            if(hasPaidInCoupe(coupeNo, signArray)) {
                if(serviceClassInt.equals("1/2")) {
                    styleClass=hasFreeInCoupe(coupeNo, signArray)?SeatCoordinate.ACCESS_TYPE.B:SeatCoordinate.ACCESS_TYPE.N;
                } else {
                    styleClass=SeatCoordinate.ACCESS_TYPE.N;
                }
            }
            if(serviceClassInt.equals("1/2")/* && !"1".equals(seatType) && !"3".equals(seatType)*/) {
                styleClass=get12StyleClass(styleClass, seatType);
            } else if(serviceClassInt.equals("1/1")/* && !"1".equals(seatType)*/) {
                styleClass=get11StyleClass(no, styleClass, seatType);
            }
        }
        return styleClass;
    }



    public SeatCoordinate.ACCESS_TYPE getStyleClass(String no, int firstNum, int lastNum) {
        SeatCoordinate.ACCESS_TYPE styleClass=SeatCoordinate.ACCESS_TYPE.N;
        int intNo = this.parseNo(no);
        if(intNo>=firstNum && intNo<=lastNum) {
            if(freePlacesMap.containsKey(no)) {
                styleClass = SeatCoordinate.ACCESS_TYPE.F;
            } else {
                styleClass = SeatCoordinate.ACCESS_TYPE.B;
            }
        }
        return styleClass;
    }

    public Map<String,String> prepareSeatsMap(List<GraphicSeatBean> seats) {
        Map<String,String> map = new HashMap<String,String>();
        if(seats!=null) {
            for(GraphicSeatBean seat: seats) {
                if(seat.getPlaces()!=null && seat.getPlaces().length>0) {
                    for(String seatNum: seat.getPlaces()) {
                        map.put(seatNum, seat.getServiceClass());
                    }
                }
            }
        }
        return map;
    }

    private SeatCoordinate.ACCESS_TYPE get12StyleClass(SeatCoordinate.ACCESS_TYPE styleClass, String seatType) {
        if(carTypeLetterShow.equals("Ц")) {
            if(!"1".equals(seatType) && !"3".equals(seatType)) {
                styleClass=SeatCoordinate.ACCESS_TYPE.N;
            }
        } else if(carTypeLetterShow.equals("К")) {
            if(!"1".equals(seatType)) {
                styleClass=SeatCoordinate.ACCESS_TYPE.N;
            }
        }
        return styleClass;
    }

    private SeatCoordinate.ACCESS_TYPE get11StyleClass(String no, SeatCoordinate.ACCESS_TYPE styleClass, String seatType) {
        if(carTypeLetterShow.equals("Ц")) {
            if(!"1".equals(seatType)) {
                styleClass=SeatCoordinate.ACCESS_TYPE.N;
            }
        } else if(carTypeLetterShow.equals("К")) {
            if(!"1".equals(seatType) || parseNo(no)%2==0) {
                styleClass=SeatCoordinate.ACCESS_TYPE.N;
            }
        }
        return styleClass;
    }

    private boolean hasPaidInCoupe(int coupeNo, JSONArray signArray) throws JSONException {
        boolean hasPaid = false;
        for (int i = 0; i < signArray.length(); i++) {
            JSONObject sign = signArray.getJSONObject(i);
            String no = sign.getString("no").toUpperCase();
            int cp = sign.getInt("cp");
            if(coupeNo == cp) {
                if(!freePlacesMap.containsKey(no)) {
                    hasPaid = true;
                    break;
                }
            }
        }
        return hasPaid;
    }

    private boolean hasFreeInCoupe(int coupeNo, JSONArray signArray) throws JSONException {
        boolean hasFree = false;
        for (int i = 0; i < signArray.length(); i++) {
            JSONObject sign = signArray.getJSONObject(i);
            String no = sign.getString("no").toUpperCase();
            int cp = sign.getInt("cp");
            if(coupeNo == cp) {
                if(isInAddPlaces(no)) {
                    hasFree = true;
                    break;
                }
            }
        }
        return hasFree;
    }

    private void parseFreePlaces(String freePlacesStr) {
        String[] freePlaces = freePlacesStr.split(",");
        for(String freePlace: freePlaces) {
            freePlace = freePlace.trim();
            String preparedPlace = prepareNo(freePlace);
            freePlacesMap.put(preparedPlace, new Object[]{ calculateCoupeType(freePlace), calcTariff(freePlace) });
        }
    }

    private Double calcTariff(String place) {
        if(places.contains(place)) {
            return tariff;
        } else {
            return placesAdd.get(prepareNo(place));
        }
    }

    public String getSeatType(String cp) {
        String type = "";
        if(!StringUtils.isEmpty(cp)) {
            int i = Integer.parseInt(cp);
            switch(i) {
                case 1:
                    type = SeatCoordinate.SEAT_TYPE.B.toString();
                    break;
                case 3:
                    type = SeatCoordinate.SEAT_TYPE.T.toString();
                    break;
                case 2:
                    type = SeatCoordinate.SEAT_TYPE.M.toString();
                    break;
                case 4:
                    type = SeatCoordinate.SEAT_TYPE.SB.toString();
                    break;
                case 5:
                    type = SeatCoordinate.SEAT_TYPE.ST.toString();
                    break;
            }
        }
        return type;
    }

  /*  private void fillFaresMap(List<FareBean> fareTypes) {
        fares = new HashMap<String,String>();
        if(fareTypes!=null && fareTypes.size()>0) {
            for(FareBean fare: fareTypes) {
                if(!StringUtils.isEmpty(fare.getPlaces())) {
                    List<PlaceBean> places = fare.getSystemPlaces();
                    for(PlaceBean place: places) {
                        String placeStr = prepareNo(place.getPlace());
                        fares.put(placeStr, fare.getTariffFormattedNoDen());
                    }
                }
            }
        }
    }*/


    private String calculateCoupeType(String no) {
        String type = "";
        if(no.length()>3) {
            char typec = no.toLowerCase().charAt(3);
            switch(typec) {
                case 'ц':
                    type = SeatCoordinate.SEX_TYPE.W.toString();
                    break;
                case 'м':
                    type = SeatCoordinate.SEX_TYPE.M.toString();
                    break;
                case 'ж':
                    type = SeatCoordinate.SEX_TYPE.F.toString();
                    break;
                case 'с':
                    type = SeatCoordinate.SEX_TYPE.H.toString();
                    break;
            }
        }
        return type;
    }

    private static int parseNo(String no) {
        no = prepareNo(no);
        no = no.replace("А", "");
        return Integer.parseInt(no);
    }


    public static String prepareNo(String no) {
        no = no.toUpperCase();
        no = no.replace("A0", "A");
        no = no.replace("А0", "А");
        if(no.length()>3) {
            no = no.substring(0,3);
        }
        if(no.startsWith("00"))
            no = no.substring(2);
        if(no.startsWith("0"))
            no = no.substring(1);
        return no;
    }
}
