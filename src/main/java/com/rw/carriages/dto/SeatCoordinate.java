package com.rw.carriages.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Информация и координаты места в вагоне")
public class SeatCoordinate {
    public enum COUPE_TYPE { C, O}
    public enum SEX_TYPE { F, M, W, H}
    public enum ACCESS_TYPE { F, B, N, I}
    public enum SEAT_TYPE {T, B, M, ST, SB}


    @ApiModelProperty(example = "1", required = true, value = "Количество пикселей вниз относительно верхнего левого угла подложки. Координата верхнего левого угла квадрата места. Пересчитывается согласно введённому параметру ширины подложки", dataType = "int")
    private int top;

    @ApiModelProperty(example = "1", required = true, value = "Количество пикселей влево относительно верхнего левого угла подложки. Координата верхнего левого угла квадрата места. Пересчитывается согласно введённому параметру ширины подложки", dataType = "int")
    private int left;

    @ApiModelProperty(example = "1", required = true, value = "Номер места", dataType = "int")
    private String no;

    @ApiModelProperty(example = "C", required = false, value = "Наименование типа купэ. Значения: C - купэ, O - отсек, если есть", dataType = "String")
    private COUPE_TYPE cp;

    @ApiModelProperty(example = "1", required = false, value = "Номер купэ/отсека, если есть", dataType = "int")
    private int cn;

    @ApiModelProperty(example = "F", required = false, value = "Гендерный тип купэ, если есть. Значения: F - женское, M - мужское, W - целое, H - смешанное", dataType = "String")
    private SEX_TYPE mwt;

    @ApiModelProperty(example = "C", required = true, value = "Доступность места. Значения: F - свободно, B - продано, N - недоступно, I - для инвалидов", dataType = "String")
    private ACCESS_TYPE at;

    @ApiModelProperty(example = "T", required = false, value = "Тип места. Значения: T - верхнее, B - нижнее, M - среднее, ST - верхнее боковое, SB - нижнее боковое", dataType = "String")
    private SEAT_TYPE st;

    @ApiModelProperty(example = "12.12", required = false, value = "Тариф места", dataType = "double")
    private double tariff;
}
