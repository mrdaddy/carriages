package com.rw.carriages.dao;

import com.rw.carriages.dto.Carriage;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.SeatCoordinate;
import com.rw.carriages.dto.request.CarriageInfo;
import com.rw.carriages.services.transform.GraphicCarriageSignBean;
import com.rw.carriages.services.transform.GraphicSeatBean;
import com.rw.carriages.services.transform.Seat46TypeBean;
import com.rw.carriages.services.utils.CarTypeUtil;
import com.rw.carriages.services.utils.RegularityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Repository
public class GraphicDao implements SQLQueries{
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public CarriageGraphic getSpecialGraphicCarriage(String train, Date depDate, CarriageInfo carriageInfo, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("TRAIN_NO", train);
        params.put("DATE", depDate);
        params.put("DAY_NUM", "%"+ RegularityUtil.getDayWeekNum(depDate)+"%");
        params.put("DAY_TYPE", "%"+RegularityUtil.getDayParity(depDate)+"%");
        params.put("CARRIAGE_NUMBER", String.valueOf(carriageInfo.getNum()));
        params.put("CARRIAGE_TYPE_CODE", CarTypeUtil.transformShowType(carriageInfo.getTypeCodeShow()));
        params.put("IS_STANDARD", "0");
        String query = MODEL_BY_TRAIN_SCHEME;
        return getGraphicCarriage(query,params,null, availableServiceClasses);

    }

    public CarriageGraphic getSubtypeGraphicCarriage(String carType, String carSubType, String carServiceClass, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("CARRIAGE_TYPE_CODE", carType);
        params.put("CARRIAGE_SUBTYPE", carSubType);
        params.put("IS_STANDARD", "0");
        String query = MODEL_BY_SUBTYPE;
        return getGraphicCarriage(query,params,carServiceClass, availableServiceClasses);
    }

    private CarriageGraphic getGraphicCarriage(String query, Map<String,Object> params, final String carServiceClass, final Set<String> availableServiceClasses) {
        CarriageGraphic graphicCarriage = null;
        try {
            graphicCarriage = jdbcTemplate.queryForObject(
                    query, params, (rs, rowNum) -> fillGraphicCarriage(rs, carServiceClass, availableServiceClasses));
        } catch (EmptyResultDataAccessException e) {
        } /*catch (IncorrectResultSizeDataAccessException e) {
            logger.debug("getGraphicCarriage: found more than 1 scheme. Params:"+params+"\n Query: "+query);
        }*/
        return graphicCarriage;
    }

    private CarriageGraphic fillGraphicCarriage(ResultSet rs, String carServiceClass, Set<String> availableServiceClasses) throws SQLException {
        CarriageGraphic graphicCarriage = new CarriageGraphic();
        graphicCarriage.setModelId(rs.getInt("ID"));
        graphicCarriage.setModelHeight(rs.getInt("GRAPHIC_MODEL_HEIGHT"));
        graphicCarriage.setModelWidth(rs.getInt("GRAPHIC_MODEL_WIDTH"));
        graphicCarriage.setSeatHeight(rs.getInt("SEAT_HEIGHT"));
        graphicCarriage.setSeatWidth(rs.getInt("SEAT_WIDTH"));
        graphicCarriage.setSignsJsonStr(rs.getString("MODEL_SIGN_COORDINATES"));
        graphicCarriage.setPlacesJsonStr(rs.getString("MODEL_SEAT_COORDINATES"));
        graphicCarriage.setSeats(getSeatsMap(rs.getInt("CARRIAGE_ID"), carServiceClass, availableServiceClasses));
        if(rs.getInt("IS_ACTIVE")==1) {
            graphicCarriage.setActive(true);
        } else {
            graphicCarriage.setActive(false);
        }
        graphicCarriage.setSeatFirstNum(rs.getInt("FIRST_SEAT"));
        graphicCarriage.setSeatLastNum(rs.getInt("LAST_SEAT"));

        return graphicCarriage;
    }

    public CarriageGraphic getSeatsGraphicCarriage(Seat46TypeBean seatsType, String carType, String carSubType, String carServiceClass, Set<String> availableServiceClasses) {
        CarriageGraphic graphicCarriage = getSeatsGraphicCarriageStrict(seatsType, carType, carSubType, carServiceClass, availableServiceClasses);
        if(!carType.equals("С") && !carType.equals("О") && (graphicCarriage==null || !graphicCarriage.isActive())) {
            graphicCarriage = getSeatsGraphicCarriageNearest(seatsType, carType, carServiceClass, availableServiceClasses);
        }
        if(graphicCarriage==null || !graphicCarriage.isActive()) {
            graphicCarriage = getSeatsGraphicCarriageStrictSpecial(seatsType, carType, carSubType, carServiceClass, availableServiceClasses);
        }
        return graphicCarriage;
    }

    public Map<Long, GraphicCarriageSignBean> getGraphicCarriageSignsMap() {
        Map<String,Object> params = new HashMap<String,Object>();
        final Map<Long,GraphicCarriageSignBean> map = new HashMap<Long,GraphicCarriageSignBean>();
        String query = GRAPHIC_CARRIGAGE_SIGNS;
        try {
            jdbcTemplate.query(
                    query, params, new RowMapper<GraphicCarriageSignBean>() {
                        public GraphicCarriageSignBean mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            GraphicCarriageSignBean sign = new GraphicCarriageSignBean();
                            sign.setId(rs.getLong("ID"));
                            sign.setCarriageSign(rs.getString("CARRIAGE_SIGN"));
                            map.put(sign.getId(), sign);
                            return sign;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
        }
        return map;
    }

    public String getTravelDirection(String trainNumber) {
        String direction = null;
        Map<String,Object> params = new HashMap<String,Object>();
        if(trainNumber.length()>4) {
            trainNumber = trainNumber.substring(0,4);
        }
        params.put("TRAIN_NO", trainNumber);
        try {
            direction = jdbcTemplate.queryForObject(
                    TRAVEL_DIRECTION, params, (rs, rowNum) -> rs.getString("TRAVEL_DIRECTION"));
        } catch (EmptyResultDataAccessException e) {
        }
        return direction;
    }

    private CarriageGraphic getSeatsGraphicCarriageStrict(Seat46TypeBean seatsType, String carType, String carSubType, String carServiceClass, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("CARRIAGE_TYPE_CODE", carType);
        if(carServiceClass==null) {
            carServiceClass = "";
        }
        params.put("LAST_SEAT", seatsType.getLastFullCar());
        params.put("IS_STANDARD", "1");
        params.put("EMPTY", "");
        params.put("SERVICE_CLASS", "%"+carServiceClass+"%");
        if((carType.equals("О")) && seatsType.getLast()<seatsType.getCount()) {
            params.put("SEAT_COUNT", seatsType.getCount());
        } else {
            params.put("SEAT_COUNT", 0);
        }
        String query = MODEL_BY_SEATS_STRICT;
        return getGraphicCarriage(query,params,null, availableServiceClasses);
    }

    private CarriageGraphic getSeatsGraphicCarriageStrictSpecial(Seat46TypeBean seatsType, String carType, String carSubType, String carServiceClass, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("CARRIAGE_TYPE_CODE", carType);
        if(carServiceClass==null) {
            carServiceClass = "";
        }
        params.put("LAST_SEAT", seatsType.getLastFullCar());
        params.put("IS_STANDARD", "0");
        params.put("EMPTY", "");
        params.put("SERVICE_CLASS", "%"+carServiceClass+"%");
        if((carType.equals("О")) && seatsType.getLast()<seatsType.getCount()) {
            params.put("SEAT_COUNT", seatsType.getCount());
        } else {
            params.put("SEAT_COUNT", 0);
        }
        String query = MODEL_BY_SEATS_STRICT_SPECIAL;
        return getGraphicCarriage(query,params,carServiceClass, availableServiceClasses);
    }


    private CarriageGraphic getSeatsGraphicCarriageNearest(Seat46TypeBean seatsType, String carType, String carServiceClass, Set<String> availableServiceClasses) {
        //TO DO
        if(carServiceClass==null) {
            carServiceClass = "";
        }
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("CARRIAGE_TYPE_CODE", carType);
        params.put("LAST_SEAT", seatsType.getLastFullCar());
        params.put("IS_STANDARD", "1");
        params.put("EMPTY", "");
        params.put("SERVICE_CLASS", "%"+carServiceClass+"%");
        if((carType.equals("О"))&& seatsType.getLast()<seatsType.getCount()) {
            params.put("SEAT_COUNT", seatsType.getCount());
        } else {
            params.put("SEAT_COUNT", 0);
        }
        String query = MODEL_BY_SEATS_STRICT_NEAREST;
        return getGraphicCarriage(query,params,null, availableServiceClasses);
    }


    private List<GraphicSeatBean> getSeatsMap(int id, String carServiceClass, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("CARRIAGE_ID", id);
        params.put("SERVICE_CLASS", "%"+carServiceClass+"%");
        params.put("CARRIAGE_SERVICE_CLASSES", availableServiceClasses);

        List<GraphicSeatBean> seats = null;
        try {
            if (carServiceClass != null && availableServiceClasses!=null && availableServiceClasses.size()>0) seats = getSeats(GRAPHIC_CARRIAGE_SEAT_LAYOUTS, params);
            if (seats == null || seats.isEmpty()) {
                seats = getSeats(GRAPHIC_CARRIAGE_SEATS, params);
            }
        } catch (EmptyResultDataAccessException e) {
        }
        return seats;
    }

    private List<GraphicSeatBean> getSeats(String query, Map<String,Object> params) {
        return jdbcTemplate.query(
                query, params, (rs, rowNum) -> {
                    GraphicSeatBean seatType = new GraphicSeatBean();
                    String serviceClass = rs.getString("CARRIAGE_SERVICE_CLASS");
                    if(serviceClass==null || serviceClass.equals("Б/К")) {
                        serviceClass = "";
                    }
                    seatType.setServiceClass(serviceClass);
                    String placesStr = rs.getString("SEATS");
                    if(!StringUtils.isEmpty(placesStr)) {
                        if(placesStr.endsWith(";")) {
                            placesStr = placesStr.substring(0, placesStr.length()-1);
                        }
                        seatType.setPlaces(placesStr.split(";"));
                    }
                    return seatType;
                });
    }

}
