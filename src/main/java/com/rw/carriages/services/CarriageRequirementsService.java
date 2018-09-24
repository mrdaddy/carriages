package com.rw.carriages.services;

import com.rw.carriages.dao.ParameterDao;
import com.rw.carriages.dto.Carriage;
import com.rw.carriages.dto.request.CarriageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CarriageRequirementsService {
    @Autowired
    ParameterDao parameterDao;

    public Carriage createCarriage(CarriageInfo carriageInfo) {
        Carriage carriage = new Carriage();
        carriage.setShowDownUpReq(calculateDownUpReq(carriageInfo));
        carriage.setShowGenderCoupeReq(calculateGenderCoupeReq(carriageInfo));
        carriage.setGroupSeatsType(calculateGroupSeatsType(carriageInfo));
        if(parameterDao.getParameterByCodeB("TICKET_PASSENGER_ADD_FIELDS")) {
            carriage.setAskPassengerBirthday(true);
            carriage.setAskPassengerCountry(true);
            carriage.setAskPassengerSex(true);
        } else {
            if(carriage.isShowGenderCoupeReq()) {
                carriage.setAskPassengerSex(true);
            }
        }
        return carriage;
    }

    public boolean isNationalCarrier(String carrier) {
        if(!StringUtils.isEmpty(carrier) && carrier.equalsIgnoreCase(parameterDao.getNationalCarrier())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean calculateDownUpReq(CarriageInfo carriageInfo) {
        if(!carriageInfo.isSaleOnTwo() && (carriageInfo.getTypeCode().equals("П") || carriageInfo.getTypeCode().equals("К")
                || carriageInfo.getServiceClassIntCode()!=null &&
                    (carriageInfo.getServiceClassIntCode().equals("2/4") || carriageInfo.getServiceClassIntCode().equals("2/3")))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean calculateGenderCoupeReq(CarriageInfo carriageInfo) {
        if(!StringUtils.isEmpty(carriageInfo.getAddSigns()) && carriageInfo.getAddSigns().contains("МЖ")) {
            return true;
        } else {
            return false;
        }
    }

    private Carriage.GROUP_TYPE calculateGroupSeatsType(CarriageInfo carriageInfo) {
        if(carriageInfo.getTypeCode().equals("О") || carriageInfo.getTypeCode().equals("С")) {
            return Carriage.GROUP_TYPE.N;
        } else {
            if(carriageInfo.getTypeCode().equals("П")) {
                return Carriage.GROUP_TYPE.CO;
            } else {
                return Carriage.GROUP_TYPE.C;
            }
        }
    }

}
