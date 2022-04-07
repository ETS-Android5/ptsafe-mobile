package com.example.ptsafe.model;

public class ObjectDetection {

    private int numberPeople;
    private int percentagePeople;
    private int status;

    public ObjectDetection() {
    }

    public ObjectDetection(int numberPeople, int percentagePeople, int status) {
        this.numberPeople = numberPeople;
        this.percentagePeople = percentagePeople;
        this.status = status;
    }

    public int getNumberPeople() {
        return numberPeople;
    }

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

    public int getPercentagePeople() {
        return percentagePeople;
    }

    public void setPercentagePeople(int percentagePeople) {
        this.percentagePeople = percentagePeople;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
