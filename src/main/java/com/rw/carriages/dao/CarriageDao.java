package com.rw.carriages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Repository
public class CarriageDao implements SQLQueries{
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public byte[] getCarriageImage(int modelId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ID", modelId);
        byte[] bytes = null;
        Blob blob = jdbcTemplate.queryForObject(
                CARRIAGE_IMAGE, params,
                (rs, rowNum) -> rs.getBlob("GRAPHIC_MODEL"));
        try{
            if(blob!=null) {
                bytes = blob.getBytes(1, (int)blob.length());
            }
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return bytes;
    }
}
