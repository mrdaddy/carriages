package com.rw.carriages.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Графическая информация о вагоне")
public class CarriageGraphic {
    @ApiModelProperty(example = "1", required = true, value = "Уникальный идентификатор записи модели вагона", dataType = "int")
    private int carriageModelId;

    @ApiModelProperty(required = true)
    private List<SeatCoordinate> seatCoordinates;
}
