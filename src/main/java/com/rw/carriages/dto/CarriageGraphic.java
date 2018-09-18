package com.rw.carriages.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Графическая информация о вагоне")
public class CarriageGraphic {
    public enum GROUP_TYPE {N, C, CO}
    @ApiModelProperty(example = "1", required = true, value = "Уникальный идентификатор записи модели вагона", dataType = "int")
    private int modelId;

    @ApiModelProperty(required = true)
    private List<SeatCoordinate> seatCoordinates;

    @ApiModelProperty(example = "true", required = true, value = "Параметр, указывающий нужно ли отображать возможность выбора верхних / нижних мест", dataType = "boolean")
    private boolean showDownUpReq;

    @ApiModelProperty(example = "true", required = true, value = "Параметр, указывающий нужно ли отображать выбор типа купе по гендерному признаку: мужское, женское, смешанное", dataType = "boolean")
    private boolean showGenderCoupeReq;

    @ApiModelProperty(example = "N", required = true, value = "Параметр, указывающий какую можно запрашивать группировку мест у экспресса. N - никакой, C - все места в одном купе, CO - возможность пользователю выбирать все места в одном купе или отсеке", dataType = "boolean")
    private GROUP_TYPE groupSeatsType;

    @ApiModelProperty(example = "true", required = true, value = "Параметр, указывающий нужно ли вводить пассажиров свой пол (мужской / женский)", dataType = "boolean")
    private boolean askPassengerSex;

    @ApiModelProperty(example = "true", required = true, value = "Параметр, указывающий нужно ли вводить всем пассажирам дату рождения", dataType = "boolean")
    private boolean askPassengerBirthday;

    @ApiModelProperty(example = "true", required = true, value = "Параметр, указывающий нужно ли вводить всем пассажирам страну гражданства", dataType = "boolean")
    private boolean askPassengerCountry;

    private PreOrder preOrder;

    @JsonIgnore
    private String placesJsonStr;
    @JsonIgnore
    private String signsJsonStr;
    @JsonIgnore
    private String seatWidth;
    @JsonIgnore
    private String seatHeight;
    @JsonIgnore
    private String modelWidth;
    @JsonIgnore
    private String modelHeight;

    @JsonIgnore
    private int seatFirstNum;
    @JsonIgnore
    private int seatLastNum;
    @JsonIgnore
    private boolean active;
    @JsonIgnore
    private String direction = null;

}
