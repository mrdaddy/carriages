package com.rw.carriages.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "Данные о вагонах и требования к координатам")
public class GraphicRequirement {
    public enum ORIENTATION {V,H};
    @ApiModelProperty(example = "201Б", required = true, value = "Номер поезда отправления", dataType = "String")
    @NotNull
    @Size(min=4,max=5)
    private String train;

    @ApiModelProperty(example = "2100276", required = true, value = "Код станции отправления", dataType = "String")
    @NotNull @Size(min=7,max=8)
    private String depStationCode;

    @ApiModelProperty(example = "2018-08-24", required = true, value = "Дата отправления со станции пассажира", dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date departureDate;

    @ApiModelProperty(example = "V", required = true, value = "Ориентация изображения вагона: V - вертикальная, H - горизонтальная", dataType = "String")
    @NotNull
    private ORIENTATION orientation;

    @ApiModelProperty(example = "100", required = false, value = "Ширина изображения в пикселях исходя из которого будут пересчитываться координаты. Под шириной понимается ширина на экране пользователя, следовательно для разных ориентаций координаты будут высчитываться по разным сторонам изображения вагона", dataType = "int")
    @Max(2000)
    private int width;

    @ApiModelProperty(required = true)
    @NotEmpty
    private List<CarriageInfo> carriageInfos;

}
