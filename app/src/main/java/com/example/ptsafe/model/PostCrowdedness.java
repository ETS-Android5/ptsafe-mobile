package com.example.ptsafe.model;

public class PostCrowdedness {

    private int stopid;
    private String routeid;
    private String departuretime;
    private int direction;
    private String day;
    private int carriagenumber;
    private float crowdednesslevel;
    private String criminalactivity;

    public PostCrowdedness() {
    }

    public PostCrowdedness(int stopId, String routeId, String departureTime, int direction, String day, int carriageNumber, float crowdednessLevel, String criminalActivity) {
        this.stopid = stopId;
        this.routeid = routeId;
        this.departuretime = departureTime;
        this.direction = direction;
        this.day = day;
        this.carriagenumber = carriageNumber;
        this.crowdednesslevel = crowdednessLevel;
        this.criminalactivity = criminalActivity;
    }

    public int getStopId() {
        return stopid;
    }

    public void setStopId(int stopId) {
        this.stopid = stopId;
    }

    public String getRouteId() {
        return routeid;
    }

    public void setRouteId(String routeId) {
        this.routeid = routeId;
    }

    public String getDepartureTime() {
        return departuretime;
    }

    public void setDepartureTime(String departureTime) {
        this.departuretime = departureTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getCarriageNumber() {
        return carriagenumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriagenumber = carriageNumber;
    }

    public float getCrowdednessLevel() {
        return crowdednesslevel;
    }

    public void setCrowdednessLevel(float crowdednessLevel) {
        this.crowdednesslevel = crowdednessLevel;
    }

    public String getCriminalActivity() {
        return criminalactivity;
    }

    public void setCriminalActivity(String criminalActivity) {
        this.criminalactivity = criminalActivity;
    }
}
