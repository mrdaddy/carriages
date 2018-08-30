package com.rw.carriages.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    @ApiModelProperty(example = "NotNull.tripInformation.train", required = true, value = "Составной код ошибки. Пример NotNull - код ошибки.tripInformation - объект .train - поле (не обязательно при общей ошибке валидации)", dataType = "String")
    private String code;

    @ApiModelProperty(example = "Не должно быть пустым", required = true, value = "Текст ошибки", dataType = "String")
    private String message;
}
