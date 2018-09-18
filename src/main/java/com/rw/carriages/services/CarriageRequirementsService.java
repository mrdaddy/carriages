package com.rw.carriages.services;

import com.rw.carriages.dao.ParameterDao;
import com.rw.carriages.dto.CarriageGraphic;
import com.rw.carriages.dto.request.Carriage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CarriageRequirementsService {
    @Autowired
    ParameterDao parameterDao;

    public CarriageGraphic createCarriageGraphic(Carriage carriage) {
        CarriageGraphic carriageGraphic = new CarriageGraphic();
        carriageGraphic.setShowDownUpReq(calculateDownUpReq(carriage));
        carriageGraphic.setShowGenderCoupeReq(calculateGenderCoupeReq(carriage));
        carriageGraphic.setGroupSeatsType(calculateGroupSeatsType(carriage));
        if(parameterDao.getParameterByCodeB("TICKET_PASSENGER_ADD_FIELDS")) {
            carriageGraphic.setAskPassengerBirthday(true);
            carriageGraphic.setAskPassengerCountry(true);
            carriageGraphic.setAskPassengerSex(true);
        } else {
            if(carriageGraphic.isShowGenderCoupeReq()) {
                carriageGraphic.setAskPassengerSex(true);
            }
        }
        return carriageGraphic;
    }

    public boolean isNationalCarrier(String carrier) {
        if(!StringUtils.isEmpty(carrier) && carrier.equalsIgnoreCase(parameterDao.getNationalCarrier())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean calculateDownUpReq(Carriage carriage) {
        if(!carriage.isOnly2m() && (carriage.getTypeCode().equals("П") || carriage.getTypeCode().equals("К")
                || carriage.getServiceClassIntCode()!=null &&
                    (carriage.getServiceClassIntCode().equals("2/4") || carriage.getServiceClassIntCode().equals("2/3")))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean calculateGenderCoupeReq(Carriage carriage) {
        if(!StringUtils.isEmpty(carriage.getAddSigns()) && carriage.getAddSigns().contains("МЖ")) {
            return true;
        } else {
            return false;
        }
    }

    private CarriageGraphic.GROUP_TYPE calculateGroupSeatsType(Carriage carriage) {
        if(carriage.getTypeCode().equals("О") || carriage.getTypeCode().equals("С")) {
            return CarriageGraphic.GROUP_TYPE.N;
        } else {
            if(carriage.getTypeCode().equals("П")) {
                return CarriageGraphic.GROUP_TYPE.CO;
            } else {
                return CarriageGraphic.GROUP_TYPE.C;
            }
        }
    }

}
