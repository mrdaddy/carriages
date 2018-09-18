package com.rw.carriages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ParameterDao {
    private static final String PARAM_INFO = "SELECT VALUE FROM ETICKET.PARAMETERS WHERE CODE=:CODE";
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public int getParameterByCodeInt(String code) {
        int val = 0;
        String valueStr = getParameterByCode(code,"0");
        if(!StringUtils.isEmpty(valueStr)) {
            val = Integer.parseInt(valueStr,10);
        }
        return val;
    }

    public boolean getParameterByCodeB(String code) {
        boolean val = false;
        String valueStr = getParameterByCode(code,"0");
        if(valueStr != null && valueStr.equals("1")) {
            val = true;
        }
        return val;
    }

    public String getParameterByCode(String code) {
        return getParameterByCode(code, null);
    }

    public String getParameterByCode(String code, String defaultValue) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CODE", code);
        String param = jdbcTemplate.queryForObject(
                PARAM_INFO, params, (rs, rowNum) -> rs.getString("VALUE"));
        if(param == null) {
            param = defaultValue;
        }
        return param;
    }

    public String getNationalCarrier() {
        return getParameterByCode("TICKET_NATIONAL_CARRIER", "");
    }
}
