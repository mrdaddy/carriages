package com.rw.carriages.services;

import com.rw.carriages.dao.PreOrderDao;
import com.rw.carriages.dto.PreOrder;
import com.rw.carriages.dto.request.Carriage;
import com.rw.carriages.services.utils.StationUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class PreOrderService {

    @Autowired
    PreOrderDao preOrderDao;

    public PreOrder createPreOrder(String train, String depStationCode, Carriage carriage) {
        PreOrder preOrder = new PreOrder();
        preOrder.setRegistrationMandatory(preOrderDao.isRegistrationMandatory(train));
        preOrder.setReturnDenied(preOrderDao.isReturnDenied(train, carriage.getTypeCode()));
        preOrder.setBeforeOrderMessage(calculateBeforeOrderMessage(carriage, depStationCode));
        return preOrder;
    }

    private PreOrder.BEFORE_ORDER_MESSAGE calculateBeforeOrderMessage(Carriage carriage, String depStationCode) {
        PreOrder.BEFORE_ORDER_MESSAGE code = null;
        /*
                    "M1 – необходимость получения ЭПД в билетной кассе Белорусской железной дороги, если станции отправления из РБ и заказ без электронной регистрации, " +
                    "M2 – необходимость получения ЭПД в билетной кассе Белорусской железной дороги, если станция отправления вне РБ и заказ без электронной регистрации, " +
                    "M3 – необходимость получения ЭПД в билетной кассе Белорусской железной дороги, если станция отправления из РБ и заказ «по глобальной цене» без электронной регистрации, "+
                    "M4 – необходимость запроса у пользователя согласия на выполнение ЭР, если для поезда разрешена ЭР, но пользователь не указал выполнение ЭР"
         */
        if(carriage.isRegistrationAllowed()) {
            code = PreOrder.BEFORE_ORDER_MESSAGE.M4;
        } else {
            if(StationUtil.isBelarusStation(depStationCode)) {
                if(carriage.isGlobalPrice()) {
                    code = PreOrder.BEFORE_ORDER_MESSAGE.M3;
                } else {
                    code = PreOrder.BEFORE_ORDER_MESSAGE.M1;
                }
            } else {
                return PreOrder.BEFORE_ORDER_MESSAGE.M2;
            }
        }
        return code;
    }
}
