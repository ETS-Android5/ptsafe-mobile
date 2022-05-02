package com.example.ptsafe.model;

public class DepartureTimesByRouteDirectionStops {

    private String routeId;
    private int stopId;
    private String departureTime;

    public DepartureTimesByRouteDirectionStops() {
    }

    public DepartureTimesByRouteDirectionStops(String routeId, int stopId, String departureTime) {
        this.routeId = routeId;
        this.stopId = stopId;
        this.departureTime = departureTime;
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

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
}
