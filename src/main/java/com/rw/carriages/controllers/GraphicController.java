package com.rw.carriages.controllers;

import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.request.GraphicRequirement;
import com.rw.carriages.services.CarriageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value="carriages/graphic", description="Cервис графического представления вагонов и информации по вагонам", tags = "Графическое представления вагонов", basePath="/carriages/graphic")
@RequestMapping(path = "/${service.version}/carriages/graphic")

public class GraphicController extends BaseController{
    @Autowired
    CarriageService carriageService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение графического представления вагонов и информации по вагонам")
    @ResponseBody
    @ResponseStatus( HttpStatus.OK )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",
                    responseHeaders = {
                            @ResponseHeader(name = "ETag", response = String.class, description = "Хеш для кэширования")}),
            @ApiResponse(code = 304, message = "Not Modified")
    })
    public List<CarriageGraphic> getCarriagesGraphic(@ApiParam GraphicRequirement graphicRequirement, @RequestHeader(name="IF-NONE-MATCH", required = false) @ApiParam(name="IF-NONE-MATCH", value = "ETag из предыдущего закэшированного запроса") String inm) {
        return carriageService.getCarriageGraphic(graphicRequirement);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE, path = "/image/{modelId}")
    @ApiOperation(value = "Получение подложки вагона в виде изображения")
    @ResponseStatus( HttpStatus.OK )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",
                    responseHeaders = {
                            @ResponseHeader(name = "ETag", response = String.class, description = "Хеш для кэширования")}),
            @ApiResponse(code = 304, message = "Not Modified")
    })
    public @ResponseBody ResponseEntity<InputStreamResource> getCarriagesGraphic(@PathVariable(value = "modelId") @ApiParam(example = "1", value = "Уникальный идентификатор записи модели вагона", required = true) int imageId, @RequestHeader(name="IF-NONE-MATCH", required = false) @ApiParam(name="IF-NONE-MATCH", value = "ETag из предыдущего закэшированного запроса") String inm) {
        return carriageService.getCarriageImage(imageId);
    }

}
