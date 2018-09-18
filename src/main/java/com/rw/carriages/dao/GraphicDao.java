package com.rw.carriages.dao;

import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.SeatCoordinate;
import com.rw.carriages.dto.request.Carriage;
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
import java.util.*;

@Repository
public class GraphicDao implements SQLQueries{
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public CarriageGraphic getSpecialGraphicCarriage(String train, Date depDate, Carriage carriage, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("TRAIN_NO", train);
        params.put("DATE", depDate);
        params.put("DAY_NUM", "%"+ RegularityUtil.getDayWeekNum(depDate)+"%");
        params.put("DAY_TYPE", "%"+RegularityUtil.getDayParity(depDate)+"%");
        params.put("CARRIAGE_NUMBER", String.valueOf(carriage.getNum()));
        params.put("CARRIAGE_TYPE_CODE", CarTypeUtil.transformShowType(carriage.getTypeCodeShow()));
        params.put("IS_ACTIVE", "1");
        params.put("IS_STANDARD", "0");
        String query = MODEL_BY_TRAIN_SCHEME;
        return getGraphicCarriage(query,params,null, availableServiceClasses);

    }

    private CarriageGraphic getGraphicCarriage(String query, Map<String,Object> params, final String carServiceClass, final Set<String> availableServiceClasses) {
        CarriageGraphic graphicCarriage = null;
        try {
            graphicCarriage = jdbcTemplate.queryForObject(
                    query, params, new RowMapper<CarriageGraphic>() {
                        public CarriageGraphic mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            return fillGraphicCarriage(rs, carServiceClass, availableServiceClasses);
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
        } catch (IncorrectResultSizeDataAccessException e) {
            //logger.debug("getGraphicCarriage: found more than 1 scheme. Params:"+params+"\n Query: "+query);
        }
        return graphicCarriage;
    }

    private CarriageGraphic fillGraphicCarriage(ResultSet rs,String carServiceClass, Set<String> availableServiceClasses) throws SQLException {
        CarriageGraphic graphicCarriage = new CarriageGraphic();
        graphicCarriage.setModelId(rs.getInt("ID"));
        graphicCarriage.setModelHeight(String.valueOf(rs.getInt("GRAPHIC_MODEL_HEIGHT")));
        graphicCarriage.setModelWidth(String.valueOf(rs.getInt("GRAPHIC_MODEL_WIDTH")));
        graphicCarriage.setSeatHeight(String.valueOf(rs.getInt("SEAT_HEIGHT")));
        graphicCarriage.setSeatWidth(String.valueOf(rs.getInt("SEAT_WIDTH")));
        graphicCarriage.setSignsJsonStr(rs.getString("MODEL_SIGN_COORDINATES"));
        graphicCarriage.setPlacesJsonStr(rs.getString("MODEL_SEAT_COORDINATES"));
        graphicCarriage.setSeatCoordinates(getSeatsMap(rs.getInt("CARRIAGE_ID"), carServiceClass, availableServiceClasses));
        if(rs.getInt("IS_ACTIVE")==1) {
            graphicCarriage.setActive(true);
        } else {
            graphicCarriage.setActive(false);
        }
        graphicCarriage.setSeatFirstNum(rs.getInt("FIRST_SEAT"));
        graphicCarriage.setSeatLastNum(rs.getInt("LAST_SEAT"));

        return graphicCarriage;
    }

    private List<SeatCoordinate> getSeatsMap(int id, String carServiceClass, Set<String> availableServiceClasses) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("CARRIAGE_ID", id);
        params.put("SERVICE_CLASS", "%"+carServiceClass+"%");
        params.put("CARRIAGE_SERVICE_CLASSES", availableServiceClasses);

        List<SeatCoordinate> seats = null;
        try {
            if (carServiceClass != null && availableServiceClasses!=null && availableServiceClasses.size()>0) seats = getSeats(GRAPHIC_CARRIAGE_SEAT_LAYOUTS, params);
            if (seats == null || seats.isEmpty()) {
                seats = getSeats(GRAPHIC_CARRIAGE_SEATS, params);
            }
			/*seats = getNamedParameterJdbcTemplate().query(
					appendDb2Schema(GRAPHIC_CARRIAGE_SEATS), params, new RowMapper<GraphicSeatType>() {
						public GraphicSeatType mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							GraphicSeatType seatType = new GraphicSeatType();
							String serviceClass = rs.getString("CARRIAGE_SERVICE_CLASS");
							if(serviceClass==null || serviceClass.equals("Б/К")) {
								serviceClass = "";
							}
							seatType.setServiceClass(serviceClass);
							String placesStr = rs.getString("SEATS");
							if(StringUtils.isNotEmpty(placesStr)) {
								if(placesStr.endsWith(";")) {
									placesStr = placesStr.substring(0, placesStr.length()-1);
								}
								seatType.setPlaces(placesStr.split(";"));
							}
							return seatType;
						}
					});*/
        } catch (EmptyResultDataAccessException e) {
        }
        return seats;
    }

    private List<SeatCoordinate> getSeats(String query, Map<String,Object> params) {
        return jdbcTemplate.query(
                query, params, new RowMapper<SeatCoordinate>() {
                    public SeatCoordinate mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        SeatCoordinate seatType = new SeatCoordinate();
                        String serviceClass = rs.getString("CARRIAGE_SERVICE_CLASS");
                        if(serviceClass==null || serviceClass.equals("Б/К")) {
                            serviceClass = "";
                        }
                     //!!!!   seatType.setServiceClass(serviceClass);
                        String placesStr = rs.getString("SEATS");
                        if(!StringUtils.isEmpty(placesStr)) {
                            if(placesStr.endsWith(";")) {
                                placesStr = placesStr.substring(0, placesStr.length()-1);
                            }
                            //!!!!     seatType.setPlaces(placesStr.split(";"));
                        }
                        return seatType;
                    }
                });
    }

}
