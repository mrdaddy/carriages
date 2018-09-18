package com.rw.carriages.services;

import com.rw.carriages.dao.GraphicDao;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.request.Carriage;
import com.rw.carriages.dto.request.GraphicRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphicService {

    @Autowired
    GraphicDao graphicDao;

    public void fillGraphic(GraphicRequirement graphicRequirement, int carNum) {
        Carriage carriage = graphicRequirement.getCarriages().get(carNum);
        graphicDao.getSpecialGraphicCarriage(graphicRequirement.getTrain(), graphicRequirement.getDepartureDate(), carriage);
    }
}
