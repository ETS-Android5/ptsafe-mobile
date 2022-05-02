package com.example.ptsafe.model;

public class RouteByDestinationType {

    private String routeId;
    private String routeShortName;
    private String routeLongName;
    private String tripHeadSign;
    private int directionId;

    public RouteByDestinationType() {
    }

    public RouteByDestinationType(String routeId, String routeShortName, String routeLongName, String tripHeadSign, int directionId) {
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.tripHeadSign = tripHeadSign;
        this.directionId = directionId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
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

    public int getDirectionId() {
        return directionId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }
}
