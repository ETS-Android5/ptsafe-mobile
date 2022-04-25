package com.example.ptsafe.model;

public class StopByRouteId {

    private String routeId;
    private int stopId;
    private String stopName;

    public StopByRouteId() {
    }

    public StopByRouteId(String routeId, int stopId, String stopName) {
        this.routeId = routeId;
        this.stopId = stopId;
        this.stopName = stopName;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
