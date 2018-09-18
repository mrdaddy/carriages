package com.rw.carriages.services;

import com.rw.carriages.dao.CarriageDao;
import com.rw.carriages.dao.ParameterDao;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.request.Carriage;
import com.rw.carriages.dto.request.GraphicRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Validated
public class CarriageService {
    @Autowired
    XMLGateService xmlGateService;

    @Autowired
    CarriageDao carriageDao;

    @Autowired
    CarriageRequirementsService carReqService;

    @Autowired
    PreOrderService preOrderService;

    @Autowired
    GraphicService graphicService;

    public List<CarriageGraphic> getCarriageGraphic(@Valid GraphicRequirement graphicRequirement) {
        List<CarriageGraphic> carriageGraphics = new ArrayList<>();
        for(int i = 0; i<graphicRequirement.getCarriages().size(); i++) {
            carriageGraphics.add(createCarriageGraphic(graphicRequirement,i));
        }
        return carriageGraphics;
    }

    public ResponseEntity<InputStreamResource> getCarriageImage(@Valid @Min(1) int modelId) {
        byte[] imageBytes = carriageDao.getCarriageImage(modelId);
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        HttpHeaders responseHeaders = new HttpHeaders();
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        responseHeaders.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(inputStreamResource, responseHeaders,
                HttpStatus.OK);

    }

    private CarriageGraphic createCarriageGraphic(GraphicRequirement graphicRequirement, int carNum) {
        Carriage carriage = graphicRequirement.getCarriages().get(carNum);
        CarriageGraphic carriageGraphic = carReqService.createCarriageGraphic(carriage);
        carriageGraphic.setPreOrder(preOrderService.createPreOrder(graphicRequirement.getTrain(), graphicRequirement.getDepStationCode(), carriage));

        if(carReqService.isNationalCarrier(carriage.getCarrier())) {
            graphicService.fillGraphic(graphicRequirement, carNum);
        }
        return carriageGraphic;
    }

}
