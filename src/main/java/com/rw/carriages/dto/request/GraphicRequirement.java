package com.rw.carriages.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "Данные о вагонах и требования к координатам")
public class GraphicRequirement {
    public enum ORIENTATION {V,H};
    @ApiModelProperty(example = "201Б", required = true, value = "Номер поезда отправления", dataType = "String")
    @NotNull
    @Size(min=4,max=5)
    private String train;

    @ApiModelProperty(example = "V", required = true, value = "Ориентация изображения вагона: V - вертикальная, H - горизонтальная", dataType = "String")
    @NotNull
    @Size(min=1,max=1)
    private ORIENTATION orientation;

    @ApiModelProperty(example = "100", required = false, value = "Ширина изображения в пикселях исходя из которого будут пересчитываться координаты. Под шириной понимается ширина на экране пользователя, следовательно для разных ориентаций координаты будут высчитываться по разным сторонам изображения вагона", dataType = "int")
    @Max(2000)
    private int width;

    @ApiModelProperty(required = true)
    @NotNull
    private List<Carriage> carriages;

}
