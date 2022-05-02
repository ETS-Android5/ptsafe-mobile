package com.example.ptsafe.model;

public class PaxByStopId {
    private int year;
    private int stopPatronageId;
    private int densityPreAmPeak;
    private int densityAmPeak;
    private int densityInterPeak;
    private int densityPmPeak;
    private int densityLatePm;

    public PaxByStopId() {
    }

    public PaxByStopId(int year, int stopPatronageId, int densityPreAmPeak, int densityAmPeak, int densityInterPeak, int densityPmPeak, int densityLatePm) {
        this.year = year;
        this.stopPatronageId = stopPatronageId;
        this.densityPreAmPeak = densityPreAmPeak;
        this.densityAmPeak = densityAmPeak;
        this.densityInterPeak = densityInterPeak;
        this.densityPmPeak = densityPmPeak;
        this.densityLatePm = densityLatePm;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getStopPatronageId() {
        return stopPatronageId;
    }

    public void setStopPatronageId(int stopPatronageId) {
        this.stopPatronageId = stopPatronageId;
    }

    public int getDensityPreAmPeak() {
        return densityPreAmPeak;
    }

    public void setDensityPreAmPeak(int densityPreAmPeak) {
        this.densityPreAmPeak = densityPreAmPeak;
    }

    public int getDensityAmPeak() {
        return densityAmPeak;
    }

    public void setDensityAmPeak(int densityAmPeak) {
        this.densityAmPeak = densityAmPeak;
    }

    public int getDensityInterPeak() {
        return densityInterPeak;
    }

    public void setDensityInterPeak(int densityInterPeak) {
        this.densityInterPeak = densityInterPeak;
    }

    public int getDensityPmPeak() {
        return densityPmPeak;
    }

    public void setDensityPmPeak(int densityPmPeak) {
        this.densityPmPeak = densityPmPeak;
    }

    public int getDensityLatePm() {
        return densityLatePm;
    }

    public void setDensityLatePm(int densityLatePm) {
        this.densityLatePm = densityLatePm;
    }
}
