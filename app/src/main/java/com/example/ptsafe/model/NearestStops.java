package com.example.ptsafe.model;

public class NearestStops {

    private int stopId;
    private String stopName;
    private float stopLat;
    private float stopLong;
    private float crowdednessDensity;
    private int totalPoliceStations;
    private float distanceInKm;

    public NearestStops() {
    }

    public NearestStops(int stopId, String stopName, float stopLat, float stopLong, float crowdednessDensity, int totalPoliceStations, float distanceInKm) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopLat = stopLat;
        this.stopLong = stopLong;
        this.crowdednessDensity = crowdednessDensity;
        this.totalPoliceStations = totalPoliceStations;
        this.distanceInKm = distanceInKm;
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

    public float getStopLat() {
        return stopLat;
    }

    public void setStopLat(float stopLat) {
        this.stopLat = stopLat;
    }

    public float getStopLong() {
        return stopLong;
    }

    public void setStopLong(float stopLong) {
        this.stopLong = stopLong;
    }

    public float getCrowdednessDensity() {
        return crowdednessDensity;
    }

    public void setCrowdednessDensity(float crowdednessDensity) {
        this.crowdednessDensity = crowdednessDensity;
    }

    public int getTotalPoliceStations() {
        return totalPoliceStations;
    }

    public void setTotalPoliceStations(int totalPoliceStations) {
        this.totalPoliceStations = totalPoliceStations;
    }

    public float getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(float distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
}
