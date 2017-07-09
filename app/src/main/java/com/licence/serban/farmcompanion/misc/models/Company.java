package com.licence.serban.farmcompanion.misc.models;

/**
 * Created by Serban on 06.03.2017.
 */

public class Company {
  private String id;
  private String name;
  private String adminArea;
  private String subAdminArea;
  private String locality;
  private String sublocality;
  private String fullAddress;
  private String country;
  private String zipCode;
  private Coordinates coordinates;
  private long createdAt;

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Company() {
  }

  public String getAdminArea() {
    return adminArea;
  }

  public void setAdminArea(String adminArea) {
    this.adminArea = adminArea;
  }

  public String getSubAdminArea() {
    return subAdminArea;
  }

  public void setSubAdminArea(String subAdminArea) {
    this.subAdminArea = subAdminArea;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public String getSublocality() {
    return sublocality;
  }

  public void setSublocality(String sublocality) {
    this.sublocality = sublocality;
  }

  public String getFullAddress() {
    return fullAddress;
  }

  public void setFullAddress(String fullAddress) {
    this.fullAddress = fullAddress;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
