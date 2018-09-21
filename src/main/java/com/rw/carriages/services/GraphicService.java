package com.rw.carriages.services;

import by.iba.railway.eticket.xml.exception.BusinessSystemException;
import by.iba.railway.eticket.xml.exception.XmlParserSystemException;
import com.rw.carriages.dao.GraphicDao;
import com.rw.carriages.dto.Carriage;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.SeatCoordinate;
import com.rw.carriages.dto.request.CarriageInfo;
import com.rw.carriages.dto.request.GraphicRequirement;
import com.rw.carriages.services.transform.GraphicCarriageSignBean;
import com.rw.carriages.services.transform.GraphicSeatBean;
import com.rw.carriages.services.transform.Seat46TypeBean;
import com.rw.carriages.services.utils.CarTypeUtil;
import com.rw.carriages.services.utils.CoordinateCalculator;
import com.rw.carriages.services.utils.SeatInfoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class GraphicService {

    @Autowired
    XMLGateService xmlGateService;

    @Autowired
    GraphicDao graphicDao;

    public CarriageGraphic getGraphicCarriage(GraphicRequirement graphicRequirement, int carNum) throws XmlParserSystemException, BusinessSystemException, JSONException {
        CarriageGraphic graphicCarriage;
        CarriageInfo carriageInfo = graphicRequirement.getCarriageInfos().get(carNum);
        Set<String> availableServiceClasses = new HashSet<>();
        for (CarriageInfo car : graphicRequirement.getCarriageInfos()) {
            if (car.getNum() == carriageInfo.getNum()) {
                if (!StringUtils.isEmpty(car.getServiceClassCode())) {
                    availableServiceClasses.add(car.getServiceClassCode());
                }
                if (!StringUtils.isEmpty(car.getAddSigns())) {
                    String addSigns = car.getAddSigns().replace("* И", "И");
                    availableServiceClasses.add(addSigns);
                }
            }
        }
        graphicCarriage = graphicDao.getSpecialGraphicCarriage(graphicRequirement.getTrain(), graphicRequirement.getDepartureDate(), carriageInfo, availableServiceClasses);
        if (graphicCarriage != null) {
            if ((graphicCarriage.getSeatFirstNum() == 0 && graphicCarriage.getSeatLastNum() == 0)
                    || graphicCarriage.getSignsJsonStr() == null || !graphicCarriage.isActive()) {
                return null;
            }
        }
        if (graphicCarriage == null && !StringUtils.isEmpty(carriageInfo.getSubType())) {
            graphicCarriage = graphicDao.getSubtypeGraphicCarriage(CarTypeUtil.transformShowType(carriageInfo.getTypeCodeShow()), carriageInfo.getSubType(), carriageInfo.getServiceClassCode(), availableServiceClasses);
            if (graphicCarriage!=null && !graphicCarriage.isActive()) {
                return null;
            }
        }

        if (graphicCarriage == null) {
            Date date = carriageInfo.getDepartureDate()!=null ? carriageInfo.getDepartureDate() : graphicRequirement.getDepartureDate();
            graphicCarriage = getG46GraphicCarriage(carriageInfo, graphicRequirement.getTrain(), date, availableServiceClasses);
        }

        if (graphicCarriage != null) {
            updateJsonParams(graphicCarriage, graphicRequirement, carNum);
            graphicCarriage.setDirection(graphicDao.getTravelDirection(graphicRequirement.getTrain()));
            if(graphicRequirement.getOrientation().equals(GraphicRequirement.ORIENTATION.H)) {
                graphicCarriage.setSeatWidth(CoordinateCalculator.calculate(graphicCarriage.getSeatWidth(),graphicRequirement.getWidth(), graphicCarriage.getModelWidth()));
                graphicCarriage.setSeatHeight(CoordinateCalculator.calculate(graphicCarriage.getSeatHeight(),graphicRequirement.getWidth(), graphicCarriage.getModelWidth()));
                graphicCarriage.setModelHeight(CoordinateCalculator.calculate(graphicCarriage.getModelHeight(),graphicRequirement.getWidth(), graphicCarriage.getModelWidth()));
                graphicCarriage.setModelWidth(graphicRequirement.getWidth());
            } else {
                graphicCarriage.setSeatWidth(CoordinateCalculator.calculate(graphicCarriage.getSeatHeight(),graphicRequirement.getWidth(), graphicCarriage.getModelWidth()));
                graphicCarriage.setSeatHeight(CoordinateCalculator.calculate(graphicCarriage.getSeatWidth(),graphicRequirement.getWidth(), graphicCarriage.getModelWidth()));
                graphicCarriage.setModelWidth(CoordinateCalculator.calculate(graphicCarriage.getModelHeight(),graphicRequirement.getWidth(), graphicCarriage.getModelWidth()));
                graphicCarriage.setModelHeight(graphicRequirement.getWidth());
            }
        }

        return graphicCarriage;
    }

    public CarriageGraphic getG46GraphicCarriage(CarriageInfo carriageInfo, String train, Date departureDate, Set<String> availableServiceClasses) throws XmlParserSystemException, BusinessSystemException {
        CarriageGraphic graphicCarriage = null;
        Seat46TypeBean seatsType = xmlGateService.getSeatsInfo(train, carriageInfo.getNum(), carriageInfo.getTypeCode(), carriageInfo.getServiceClassCode(), departureDate);
        if (seatsType != null) {
            graphicCarriage = graphicDao.getSeatsGraphicCarriage(seatsType, CarTypeUtil.transformShowType(carriageInfo.getTypeCodeShow()), carriageInfo.getSubType(), carriageInfo.getServiceClassCode(), availableServiceClasses);
            if (graphicCarriage != null && graphicCarriage.isActive()) {
                int first = Math.max(seatsType.getFirst(), graphicCarriage.getSeatFirstNum());
                int last = Math.min(seatsType.getLast(), graphicCarriage.getSeatLastNum());
                // updateFirstLastNums(first,last,options);
                graphicCarriage.setSeatFirstNum(first);
                graphicCarriage.setSeatLastNum(last);
            } else if (seatsType.getFirst() != null && seatsType.getLast() != null) {
                //updateFirstLastNums(seatsType.getFirst(),seatsType.getLast(),options);
                graphicCarriage = null;
            }
            // options.setHidePlacesInterval(false);
        } /*else {
            if("Ц".equals(carType)) {
                options.setHidePlacesInterval(true);
            } else {
                options.setHidePlacesInterval(false);
            }
        }*/
        return graphicCarriage;
    }


    private void updateJsonParams(CarriageGraphic graphicCarriage, GraphicRequirement graphicRequirement, int carNum) throws JSONException {
        if (graphicCarriage != null) {
            if (!StringUtils.isEmpty(graphicCarriage.getSignsJsonStr())) {
                graphicCarriage.setSignsJsonStr(getExtendedSignsJsonStr(graphicCarriage.getSignsJsonStr()));
            }
            if (!StringUtils.isEmpty(graphicCarriage.getPlacesJsonStr())) {
                graphicCarriage.setSeatCoordinates(getExtendedPlacesJsonStr(graphicRequirement, carNum, graphicCarriage));
            }
        }
    }

    private String getExtendedSignsJsonStr(String jsonStr) throws JSONException {
        JSONArray resultArray = new JSONArray();
        //try {
        JSONArray signArray = new JSONArray(jsonStr);
        Map<Long, GraphicCarriageSignBean> carriageSigns = graphicDao.getGraphicCarriageSignsMap();
        for (int i = 0; i < signArray.length(); i++) {
            JSONObject sign = signArray.getJSONObject(i);
            Long id = sign.getLong("id");
            GraphicCarriageSignBean carriageSign = carriageSigns.get(id);
            // log.debug(id.toString());
            sign.put("name", carriageSign.getCarriageSign());
            resultArray.put(sign);

        }

        return resultArray.toString();
    }


    private List<SeatCoordinate> getExtendedPlacesJsonStr(GraphicRequirement graphicRequirement, int carNum,  CarriageGraphic graphicCarriage) throws JSONException {
        String jsonStr = graphicCarriage.getPlacesJsonStr();
        int firstNum = graphicCarriage.getSeatFirstNum();
        int lastNum = graphicCarriage.getSeatLastNum();
        List<GraphicSeatBean> seats = graphicCarriage.getSeats();
        int width = graphicCarriage.getModelWidth();
        CarriageInfo carriageInfo = graphicRequirement.getCarriageInfos().get(carNum);
        List<SeatCoordinate> seatCoordinates = new ArrayList<>();
        SeatInfoProcessor seatProcessor = new SeatInfoProcessor(carriageInfo, calcAddFreePlaces(graphicRequirement, carNum));
        String serviceClass = carriageInfo.getServiceClassCode();

        JSONArray signArray = new JSONArray(jsonStr);
        Map<String, String> map = seatProcessor.prepareSeatsMap(seats);
        boolean hasSeats = false;
        for (int i = 0; i < signArray.length(); i++) {
            JSONObject sign = signArray.getJSONObject(i);

            SeatCoordinate seatCoordinate = new SeatCoordinate();

            String no = sign.getString("no").toUpperCase();
            seatCoordinate.setNo(no);
            String cpName;
            if (carriageInfo.getTypeCode().equals("О") || carriageInfo.getTypeCode().equals("П")) {
                seatCoordinate.setCp(SeatCoordinate.COUPE_TYPE.O);
                cpName = "bx";
            } else {
                seatCoordinate.setCp(SeatCoordinate.COUPE_TYPE.C);
                cpName = "cp";
            }
            if (sign.has(cpName) && sign.getString(cpName) != null) {
                seatCoordinate.setCn(sign.getInt(cpName));
            }

            if (sign.has("tp") && !StringUtils.isEmpty(sign.getString("tp"))) {
                seatCoordinate.setSt(SeatCoordinate.SEAT_TYPE.valueOf(sign.getString("tp").toUpperCase()));
            }

            String coupeType = seatProcessor.getCoupType(no);
            if (!StringUtils.isEmpty(coupeType)) {
                seatCoordinate.setMwt(SeatCoordinate.SEX_TYPE.valueOf(coupeType));
            }

            String seatServiceClass = map.get(seatProcessor.prepareNo(no));
            if (seatServiceClass == null) {
                seatServiceClass = "";
            }
            if (seatServiceClass != null && (StringUtils.isEmpty(seatServiceClass) || StringUtils.isEmpty(serviceClass) || serviceClass.equals(seatServiceClass))) {
                SeatCoordinate.ACCESS_TYPE styleClass = seatProcessor.getStyleClass(no, firstNum, lastNum);
                if (!SeatCoordinate.ACCESS_TYPE.N.equals(styleClass) && !StringUtils.isEmpty(carriageInfo.getServiceClassIntCode())) {
                    styleClass = seatProcessor.getSingleDoubleStyleClass(no, styleClass, seatCoordinate.getCp().toString(), seatCoordinate.getCn(), signArray);
                }
                if (!SeatCoordinate.ACCESS_TYPE.F.equals(styleClass) && "1/2".equals(carriageInfo.getServiceClassIntCode())) {
                    styleClass = seatProcessor.getStyleClassByAddPlaces(no, styleClass);
                }
                if (!SeatCoordinate.ACCESS_TYPE.N.equals(styleClass)) {
                    hasSeats = true;
                }
                seatCoordinate.setAt(styleClass);
            } else if (seatServiceClass != null && seatServiceClass.equals("И")) {
                seatCoordinate.setAt(SeatCoordinate.ACCESS_TYPE.I);
            } else {
                seatCoordinate.setAt(SeatCoordinate.ACCESS_TYPE.N);
            }

            seatCoordinate.setTariff(seatProcessor.getTariff(no));
            if(graphicRequirement.getOrientation().equals(GraphicRequirement.ORIENTATION.H)) {
                seatCoordinate.setTop(CoordinateCalculator.calculate(sign.getInt("top"),graphicRequirement.getWidth(), width));
                seatCoordinate.setLeft(CoordinateCalculator.calculate(sign.getInt("left"),graphicRequirement.getWidth(), width));
            } else {
                seatCoordinate.setTop(CoordinateCalculator.calculate(sign.getInt("top"),graphicRequirement.getWidth(), width));
                seatCoordinate.setLeft(CoordinateCalculator.calculate(sign.getInt("left"),graphicRequirement.getWidth(), width));
            }

            seatCoordinates.add(seatCoordinate);
        }
        if (!hasSeats) {
            seatCoordinates = null;
        }
        return seatCoordinates;
    }

    private Map<String,Double> calcAddFreePlaces(GraphicRequirement graphicRequirement, int carNum) {
        Map<String,Double> addPlaces = new HashMap<>();
        CarriageInfo carriageInfo = graphicRequirement.getCarriageInfos().get(carNum);
        for(int i = 0; i< graphicRequirement.getCarriageInfos().size(); i++) {
            CarriageInfo carInfo = graphicRequirement.getCarriageInfos().get(i);
            if(i != carNum && carriageInfo.getNum() == carInfo.getNum()) {
                if(!StringUtils.isEmpty(carInfo.getFreeSeats())) {
                    String[] seats = carInfo.getFreeSeats().split(",");
                    if(seats!=null) {
                        for(String seat: seats) {
                            addPlaces.put(SeatInfoProcessor.prepareNo(seat),carInfo.getTariff());
                        }
                    }
                }
            }
        }
        return addPlaces;
    }

}
