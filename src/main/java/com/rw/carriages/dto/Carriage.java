package com.rw.carriages.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(description = "Информация о вагоне")
@NoArgsConstructor
public class Carriage {
    public enum GROUP_TYPE {N, C, CO}


    private CarriageGraphic carriageGraphic;

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

}
