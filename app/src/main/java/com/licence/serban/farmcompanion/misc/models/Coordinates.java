package com.licence.serban.farmcompanion.misc.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Serban on 06.04.2017.
 */

public class Coordinates {
  private double latitude;
  private double longitude;

  public Coordinates() {
  }

  public Coordinates(Location location) {
    this.latitude = location.getLatitude();
    this.longitude = location.getLongitude();
  }

  public Coordinates(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public LatLng toLatLng() {
    return new LatLng(getLatitude(), getLongitude());
  }
}
