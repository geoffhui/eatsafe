package com.example.eatsafe;

public class itemModel {
    private String title;
    private String trackingNumber;
    private String date;
    private String inspType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String violLump;
    private String address;
    private String name;


    public itemModel (String title, String date, String inspType, int numCritical, int numNonCritical, String hazardRating, String violLump, String address){
        this.title = title;
        this.date = date;
        this.inspType = inspType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violLump = violLump;
        this.address = address;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTrackingNumber(){
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber){
        this.trackingNumber = trackingNumber;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getInspType(){
        return inspType;
    }

    public void setInspType(String inspType){
        this.inspType = inspType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public String getViolLump() {
        return violLump;
    }

    public void setViolLump(String violLump) {
        this.violLump = violLump;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
