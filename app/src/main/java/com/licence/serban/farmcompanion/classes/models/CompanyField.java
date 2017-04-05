package com.licence.serban.farmcompanion.classes.models;

/**
 * Created by Serban on 17.03.2017.
 */

public class CompanyField {
    private String id;
    private String name;
    private String location;
    private String cropStatus;
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCropStatus() {
        return cropStatus;
    }

    public void setCropStatus(String cropStatus) {
        this.cropStatus = cropStatus;
    }

    private double area;
    private String ownership;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }
}
