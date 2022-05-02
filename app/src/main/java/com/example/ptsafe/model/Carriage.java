package com.example.ptsafe.model;

public class Carriage {

    private int carriageNumber;
    private double averageCrowdednessLevel;

    public Carriage() {
    }

    public Carriage(int carriageNumber, double averageCrowdednessLevel) {
        this.carriageNumber = carriageNumber;
        this.averageCrowdednessLevel = averageCrowdednessLevel;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public double getAverageCrowdednessLevel() {
        return averageCrowdednessLevel;
    }

    public void setAverageCrowdednessLevel(double averageCrowdednessLevel) {
        this.averageCrowdednessLevel = averageCrowdednessLevel;
    }
}
