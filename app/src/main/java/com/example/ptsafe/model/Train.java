package com.example.ptsafe.model;

public class Train {

    private String routeId;
    private String routeLongName;
    private String tripHeadSign;
    private String departureTime;

    public Train() {
    }

    public Train(String routeId, String routeLongName, String tripHeadSign, String departureTime) {
        this.routeId = routeId;
        this.routeLongName = routeLongName;
        this.tripHeadSign = tripHeadSign;
        this.departureTime = departureTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getTripHeadSign() {
        return tripHeadSign;
    }

    public void setTripHeadSign(String tripHeadSign) {
        this.tripHeadSign = tripHeadSign;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
}
