package com.rw.carriages.controllers;

import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.ErrorMessage;
import com.rw.carriages.dto.request.GraphicRequirement;
import com.rw.carriages.services.GraphicService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class, responseContainer = "List")
})
@RestController
@Api(value="carriages/graphic", description="Cервис графического представления вагонов и информации по вагонам", tags = "Графическое представления вагонов", basePath="/carriages/graphic")
@RequestMapping(path = "/${service.version}/carriages/graphic")

public class GraphicController {
    @Autowired
    GraphicService graphicService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение графического представления вагонов и информации по вагонам")
    @ResponseBody
    @ResponseStatus( HttpStatus.OK )
    public List<CarriageGraphic> getCarriagesGraphic(@ApiParam GraphicRequirement graphicRequirement) {
        return graphicService.getCarriageGraphic(graphicRequirement);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE, path = "/image/{modelId}")
    @ApiOperation(value = "Получение подложки вагона в виде изображения")
    @ResponseBody
    @ResponseStatus( HttpStatus.OK )
    public byte[] getCarriagesGraphic(@PathVariable(value = "modelId") @ApiParam(example = "1", value = "Уникальный идентификатор записи модели вагона", required = true) int imageId) {
        return graphicService.getCarriageImage(imageId);
    }

}
