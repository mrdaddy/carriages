package com.rw.carriages.services;

import com.rw.carriages.dao.CarriageDao;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.request.GraphicRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class GraphicService {
    @Autowired
    XMLGateService xmlGateService;

    @Autowired
    CarriageDao carriageDao;

    public List<CarriageGraphic> getCarriageGraphic(@Valid GraphicRequirement graphicRequirement) {
        return new ArrayList<CarriageGraphic>();
    }

    public byte[] getCarriageImage(@Valid @Min(1) int modelId) {
        return new byte[100];
    }
}
