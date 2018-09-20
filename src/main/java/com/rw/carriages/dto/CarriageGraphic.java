package com.rw.carriages.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rw.carriages.services.transform.GraphicSeatBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(description = "Графическая информация о вагоне")
@NoArgsConstructor
public class CarriageGraphic {

    @ApiModelProperty(example = "1", required = true, value = "Уникальный идентификатор записи модели вагона", dataType = "int")
    private int modelId;

    @ApiModelProperty(required = true)
    private List<SeatCoordinate> seatCoordinates;

    @JsonIgnore
    private String placesJsonStr;
    @JsonIgnore
    private String signsJsonStr;

    @ApiModelProperty(example = "1", required = true, value = "Расчитанная ширина элемента места в пикселах", dataType = "int")
    private int seatWidth;

    @ApiModelProperty(example = "1", required = true, value = "Расчитанная высота элемента места в пикселах", dataType = "int")
    private int seatHeight;

    @ApiModelProperty(example = "1", required = true, value = "Расчитанная ширина подложки", dataType = "int")
    private int modelWidth;

    @ApiModelProperty(example = "1", required = true, value = "Расчитанная высота подложки", dataType = "int")
    private int modelHeight;

    @ApiModelProperty(example = "1", required = false, value = "Номер первого места в вагоне", dataType = "int")
    private int seatFirstNum;

    @ApiModelProperty(example = "20", required = false, value = "Номер последнего места в вагоне", dataType = "int")
    private int seatLastNum;
    @JsonIgnore
    private boolean active;

    @JsonIgnore
    private List<GraphicSeatBean> seats;

    @ApiModelProperty(example = "1", required = false, value = "Направление движения поезда", dataType = "String")
    private String direction = null;
}
