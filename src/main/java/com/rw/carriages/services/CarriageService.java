package com.rw.carriages.services;

import by.iba.railway.eticket.xml.exception.BusinessSystemException;
import by.iba.railway.eticket.xml.exception.XmlParserSystemException;
import com.rw.carriages.dao.CarriageDao;
import com.rw.carriages.dto.Carriage;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.request.CarriageInfo;
import com.rw.carriages.dto.request.GraphicRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class CarriageService {

    @Autowired
    CarriageDao carriageDao;

    @Autowired
    CarriageRequirementsService carReqService;

    @Autowired
    PreOrderService preOrderService;

    @Autowired
    GraphicService graphicService;

    public List<Carriage> getCarriageGraphic(@Valid GraphicRequirement graphicRequirement) throws XmlParserSystemException, BusinessSystemException, JSONException {
        List<Carriage> carriages = new ArrayList<>();
        for(int i = 0; i<graphicRequirement.getCarriageInfos().size(); i++) {
            carriages.add(createCarriageGraphic(graphicRequirement,i));
        }
        return carriages;
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

    private Carriage createCarriageGraphic(GraphicRequirement graphicRequirement, int carNum) throws XmlParserSystemException, BusinessSystemException, JSONException {
        CarriageInfo carriageInfo = graphicRequirement.getCarriageInfos().get(carNum);
        Carriage carriage = carReqService.createCarriage(carriageInfo);
        if(carReqService.isNationalCarrier(carriageInfo.getCarrier())) {
            carriage.setCarriageGraphic(graphicService.getGraphicCarriage(graphicRequirement, carNum));
        }
        carriage.setPreOrder(preOrderService.createPreOrder(graphicRequirement.getTrain(), graphicRequirement.getDepStationCode(), carriageInfo));
      //  String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));

        return carriage;
    }

}
