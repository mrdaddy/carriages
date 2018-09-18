package com.rw.carriages.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@ApiModel(description = "Данные о вагоне из справок расписания")
public class Carriage {
    @ApiModelProperty(example = "1", required = true, value = "Номер вагона", dataType = "int")
    @NotNull @Max(10000)
    private int num;

    @ApiModelProperty(example = "2018-08-24", required = true, value = "Дата отправления вагона. Данная дата должна высчитываться по следующей формуле: Если заполнена дата для конкретного вагона : <Modificators><DepartureTrain Date=\"01.01.2014\" /></Modificators>, то давать её, иначе, если заполнена дата в теге Train: <DepartureTrain Date=\"21.12.2009\" />, давать её, иначе - дата отправления со станции пассажира)", dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date departureDate;

    @ApiModelProperty(example = "К", required = true, value = "Тип вагона - код", dataType = "String")
    @NotNull
    @Size(min=1, max = 1)
    private String typeCode;

    @ApiModelProperty(example = "РИЦ", required = true, value = "Тип вагона для отображения, заполняется, если отличается от обычного типа вагона (параметр TypeShow из ответа шлюза) - код", dataType = "String")
    @Size(max = 3)
    private String typeCodeShow;

    @ApiModelProperty(example = "1Б", required = false, value = "Класс обслуживания - код", dataType = "String")
    @Size(max = 3)
    private String serviceClassCode;

    @ApiModelProperty(example = "1/1", required = false, value = "Класс обслуживания по международной классификации - код", dataType = "String")
    @Size(max = 3)
    private String serviceClassIntCode;

    @ApiModelProperty(example = "У0", required = false, value = "Дополнительные признаки вагона", dataType = "String")
    @Size(max = 16)
    private String addSigns;

    @ApiModelProperty(example = "БЧ", required = false, value = "Перевозчик вагона", dataType = "String")
    @Size(max = 32)
    private String carrier;

    @ApiModelProperty(example = "002,004,006", required = false, value = "Номера свободных мест", dataType = "String")
    @Size(max = 1000)
    private String freeSeats;

    @NotNull @ApiModelProperty(example = "false", required = true, value = "Флаг, указывающий, что продажа на данный поезд производится по глобальным ценам", dataType = "boolean")
    private boolean isGlobalPrice;

    @NotNull @ApiModelProperty(example = "true", required = true, value = "Флаг, указывающий, разрешена ли для данного вагона и поезда электронная регистрация", dataType = "boolean")
    private boolean isRegistrationAllowed;

    @ApiModelProperty(example = "12.12", value = "Тариф места", dataType = "double")
    @Max(10000)
    private double tariff;

    @ApiModelProperty(example = "false", required = false, value = "В данном вагоне места продаются только по 2", dataType = "boolean")
    private boolean only2m;
}
