package com.rw.carriages.services.transform;

import by.iba.railway.eticket.xml.objs.response.type.G46.SeatsG46Type;

public class Seat46TypeBean  {
    private int lastFullCar;
    private int firstFullCar;
    private SeatsG46Type seatType;

    public Seat46TypeBean(SeatsG46Type seatType) {
        if(seatType!=null) {
            this.seatType = seatType;
        } else {
            this.seatType = new SeatsG46Type();
        }
    }

    public Integer getFirst() {
        return seatType.getFirst();
    }

    public Integer getCount() {
        return seatType.getCount();
    }

    public Integer getLast() {
        return seatType.getLast();
    }

    public int getLastFullCar() {
        return lastFullCar;
    }
    public void setLastFullCar(int lastFullCar) {
        this.lastFullCar = lastFullCar;
    }
    public int getFirstFullCar() {
        return firstFullCar;
    }
    public void setFirstFullCar(int firstFullCar) {
        this.firstFullCar = firstFullCar;
    }

}