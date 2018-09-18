package com.rw.carriages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PreOrderDao implements SQLQueries {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public boolean isRegistrationMandatory(String trainNum) {
        boolean isTrainWithForcedER = false;
        Map<String, Object> params = new HashMap<String, Object>();
        String shortTrainNum = trainNum;
        if (shortTrainNum.length() > 4) {
            shortTrainNum = shortTrainNum.substring(0, 4);
        }
        params.put("TRAIN_NO", shortTrainNum + "%");

        try {
            List<String> trains = jdbcTemplate.query(
                    FORCED_ER, params, (rs, rowNum) -> rs.getString("TRAIN_NO"));
            for (String train : trains) {
                if (trainNum.startsWith(train)) {
                    isTrainWithForcedER = true;
                    break;
                }
            }

        } catch (EmptyResultDataAccessException e1) {
        }
        return isTrainWithForcedER;
    }

    public boolean isReturnDenied(String trainNo, String carType) {
        boolean returnAllowed = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TRAIN_NO", (trainNo.length()>4?trainNo.substring(0, 4):trainNo)+"%");
        params.put("CARRIAGE_TYPE", carType.substring(0, 1).toUpperCase());
        String query = SQLQueries.CHECK_RETURN_FORBIDDEN;
        try {
            int id = jdbcTemplate.queryForObject(query, params,Integer.class);
            if(id>0) {
                returnAllowed = false;
            }
        } catch (EmptyResultDataAccessException e1) { }
        return returnAllowed;
    }
}
