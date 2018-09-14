package com.rw.carriages.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Информация об ошибке. В поле code первая часть - тип ошибки. Значения: validation - ошибки валидации входных значений, system - системные ошибки сервера СППД (например, соединение с БД или LDAP), express - ошибки или проблемы соединения со шлюзом XML или самой системы Экспресс, epd - ошибки или проблемы соединения с сервером ЭПД")
public class ErrorMessage {
    @ApiModelProperty(example = "validation.NotNull.tripInformation.train", required = true, value = "Составной код ошибки. Пример NotNull - код ошибки.tripInformation - объект (только для ошибок валидации) .train - поле (только для ошибок валидации, не обязательно при общей ошибке валидации)", dataType = "String")
    private String code;

    @ApiModelProperty(example = "Не должно быть пустым", required = true, value = "Текст ошибки", dataType = "String")
    private String message;
}
