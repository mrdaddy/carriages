package com.rw.carriages.services;

import by.iba.railway.eticket.xml.RailWayServiceFactory;
import by.iba.railway.eticket.xml.exception.BusinessSystemException;
import by.iba.railway.eticket.xml.exception.XmlParserSystemException;
import by.iba.railway.eticket.xml.objs.response.express.TrainSchemeG46Response;
import by.iba.railway.eticket.xml.objs.response.type.G46.CarType;
import by.iba.railway.eticket.xml.services.EticketService;
import by.iba.railway.eticket.xml.services.ExpressService;
import com.rw.carriages.services.transform.Seat46TypeBean;
import com.rw.carriages.services.utils.DateTimeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class XMLGateService {
    private ExpressService ws = null;
    private EticketService wsP = null;

    @Value("${sppdpars.gateway.url}")
    private String gatewayUrl;

    @Value("${sppdpars.gateway.login}")
    private String gatewayLogin;

    @Value("${sppdpars.gateway.password}")
    private String gatewayPassword;

    @Value("${sppdpars.gateway.schedule.timeout}")
    private int scheduleTimeout;

    @Value("${sppdpars.gateway.ticket.timeout}")
    private int ticketTimeout;

    @Value("${sppdpars.gateway.debug.g46}")
    private boolean debug;

    protected void connectToService() {
        if (ws == null) {

            /*by.iba.buyticket.model.service.StatisticService statisticService = null;
            if(trainService!=null) {
                statisticService = trainService.getStatisticService();
            }*/
            RailWayServiceFactory rw = new RailWayServiceFactory(ticketTimeout,null);
            ws = rw.getExpressService(gatewayUrl, gatewayLogin, gatewayPassword,true);
            wsP = rw.getEticketService(gatewayUrl, gatewayLogin, gatewayPassword,true);
        }
    }

    public Seat46TypeBean getSeatsInfo(String trainNumber, int carNumber, String carType, String carClassService, Date date) throws XmlParserSystemException, BusinessSystemException {
        connectToService();
        String dateStr = DateTimeConverter.getDateFullString(date);
        TrainSchemeG46Response response = getResponse(trainNumber, dateStr);
        Seat46TypeBean seatType = null;
        if(response!=null && response.getTrain()!=null && response.getTrain().getCar()!=null) {
            int maxLastNum = 0;
            int minLastNum = 9999;
            for(CarType car: response.getTrain().getCar()) {
                int car46Number = Integer.parseInt(car.getNumber(), 10);
                if(car46Number == carNumber && car.getType().equals(carType)) {
                    if((StringUtils.isEmpty(carClassService) || car.getClassService()==null || car.getClassService().getType().equals(carClassService))) {
                        if(seatType == null) {
                            seatType = new Seat46TypeBean(car.getSeats());
                        }
                    }
                    maxLastNum = Math.max(maxLastNum, car.getSeats().getLast());
                    minLastNum = Math.min(minLastNum, car.getSeats().getFirst());
                }
            }
            //only for C carriages get max for 2P service class
            if(seatType != null && "С".equals(carType)) {
                if(maxLastNum > 0) {
                    seatType.setLastFullCar(maxLastNum);
                } else {
                    seatType.setLastFullCar(seatType.getLast());
                }
                if(minLastNum < 9999 && minLastNum > 0) {
                    seatType.setFirstFullCar(minLastNum);
                } else {
                    seatType.setFirstFullCar(seatType.getFirst());
                }
            } else if(seatType != null) {
                seatType.setFirstFullCar(seatType.getFirst());
                seatType.setLastFullCar(seatType.getLast());

            }
        }
        return seatType;
    }

    private TrainSchemeG46Response getResponse(String trainNumber,String dateStr) throws XmlParserSystemException, BusinessSystemException {
       /* TrainSchemeG46Response response = null;
        CacheExpressService cacheExpressService = getCacheExpressService();
        if(!withoutCache && cacheExpressService!=null) {
            response = cacheExpressService.getCache(RequestTypes.G46, new String[]{trainNumber,dateStr}, TrainSchemeG46Response.class, debug);
        }
        if(response==null) {
            response = ws.getTrainSchemeInfo(trainNumber, dateStr);
            if(!withoutCache && cacheExpressService!=null && response!=null && response.getError()==null && !StringUtils.isEmpty(response.getXmlResponse())) {
                response.setXmlResponse("");
                cacheExpressService.saveCache(RequestTypes.G46, new String[]{trainNumber,dateStr}, response);
            }
        }*/
        TrainSchemeG46Response response = ws.getTrainSchemeInfo(trainNumber, dateStr);
        return response;
    }

}
