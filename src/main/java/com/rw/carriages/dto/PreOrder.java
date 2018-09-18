package com.rw.carriages.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Бизнес-правила, необходимых для выполнения при оформлении и оплате мест из данного вагона")
public class PreOrder {
    public enum BEFORE_ORDER_MESSAGE {M1, M2, M3, M4}
    @ApiModelProperty(example = "false", required = true, value = "Признак, нужна ли принудительная ЭР на указанный поезд и вагон", dataType = "boolean")
    private boolean isRegistrationMandatory;

    @ApiModelProperty(example = "false", required = true, value = "Признак, есть ли запрет возврата ЭПД на указанный поезд и вагон", dataType = "boolean")
    private boolean isReturnDenied;

    @ApiModelProperty(required = false, example = "M1", value = "Необходимость отобразить пользователю дополнительную информацию перед заказом. Значения: " +
            "M1 – необходимость получения ЭПД в билетной кассе Белорусской железной дороги, если станции отправления из РБ и заказ без электронной регистрации, " +
            "M2 – необходимость получения ЭПД в билетной кассе Белорусской железной дороги, если станция отправления вне РБ и заказ без электронной регистрации, " +
            "M3 – необходимость получения ЭПД в билетной кассе Белорусской железной дороги, если станция отправления из РБ и заказ «по глобальной цене» без электронной регистрации, "+
            "M4 – необходимость запроса у пользователя согласия на выполнение ЭР, если для поезда разрешена ЭР, но пользователь не указал выполнение ЭР"
            , dataType = "String")
    private BEFORE_ORDER_MESSAGE beforeOrderMessage;
}
