package com.rw.carriages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class CarriageDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

}
