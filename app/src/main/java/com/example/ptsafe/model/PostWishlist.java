package com.example.ptsafe.model;

public class PostWishlist {

    private String sourcename;
    private String destinationname;
    private int stopid;
    private String routeid;
    private String departuretime;
    private int carriagenumber;

    public PostWishlist() {

    }

    public PostWishlist(String sourcename, String destinationname, int stopid, String routeid, String departuretime, int carriagenumber) {
        this.sourcename = sourcename;
        this.destinationname = destinationname;
        this.stopid = stopid;
        this.routeid = routeid;
        this.departuretime = departuretime;
        this.carriagenumber = carriagenumber;
    }

    public String getSourcename() {
        return sourcename;
    }

    public void setSourcename(String sourcename) {
        this.sourcename = sourcename;
    }

    public String getDestinationname() {
        return destinationname;
    }

    public void setDestinationname(String destinationname) {
        this.destinationname = destinationname;
    }

    public int getStopid() {
        return stopid;
    }

    public void setStopid(int stopid) {
        this.stopid = stopid;
    }

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public String getDeparturetime() {
        return departuretime;
    }

    public void setDeparturetime(String departuretime) {
        this.departuretime = departuretime;
    }

    public int getCarriagenumber() {
        return carriagenumber;
    }

    public void setCarriagenumber(int carriagenumber) {
        this.carriagenumber = carriagenumber;
    }
}
