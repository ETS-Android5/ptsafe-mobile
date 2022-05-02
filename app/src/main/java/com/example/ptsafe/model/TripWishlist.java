package com.example.ptsafe.model;

public class TripWishlist {

    private String wishlistId;
    private String sourceName;
    private String destinationName;
    private String stopName;
    private String routeLongName;
    private String departureTime;
    private int carriageNumber;

    public TripWishlist() {
    }

    public TripWishlist(String wishlistId, String sourceName, String destinationName, String stopName, String routeLongName, String departureTime, int carriageNumber) {
        this.wishlistId = wishlistId;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.stopName = stopName;
        this.routeLongName = routeLongName;
        this.departureTime = departureTime;
        this.carriageNumber = carriageNumber;
    }

    public String getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(String wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }
}
