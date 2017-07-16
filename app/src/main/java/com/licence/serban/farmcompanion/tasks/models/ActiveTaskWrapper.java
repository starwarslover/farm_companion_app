package com.licence.serban.farmcompanion.tasks.models;

import com.licence.serban.farmcompanion.misc.models.Coordinates;

/**
 * Created by Serban on 17.07.2017.
 */

public class ActiveTaskWrapper {
  private int currentState;
  private String emp_name;
  private Coordinates live_coordinates;
  private double speed;
  private long startDate;
  private long timeStopped;
  private double travelDistance;

  public ActiveTaskWrapper() {
  }

  public int getCurrentState() {
    return currentState;
  }

  public void setCurrentState(int currentState) {
    this.currentState = currentState;
  }

  public String getEmp_name() {
    return emp_name;
  }

  public void setEmp_name(String emp_name) {
    this.emp_name = emp_name;
  }

  public Coordinates getLive_coordinates() {
    return live_coordinates;
  }

  public void setLive_coordinates(Coordinates live_coordinates) {
    this.live_coordinates = live_coordinates;
  }

  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public long getStartDate() {
    return startDate;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public long getTimeStopped() {
    return timeStopped;
  }

  public void setTimeStopped(long timeStopped) {
    this.timeStopped = timeStopped;
  }

  public double getTravelDistance() {
    return travelDistance;
  }

  public void setTravelDistance(double travelDistance) {
    this.travelDistance = travelDistance;
  }
}
