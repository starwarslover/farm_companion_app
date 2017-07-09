package com.licence.serban.farmcompanion.misc.weather.models;

import java.util.Date;
import java.util.HashMap;

public class WeatherCondition {
  public enum ConditionType {
    CLEAR, FEW_CLOUDS, CLOUDS, MIST, SHOWER, RAIN, STORM, SNOW
  }

  public static HashMap<ConditionType, Integer> dangerLevel = new HashMap<>();
  private String city;
  private Date date;

  private double temp;
  private double tempMin;
  private double tempMax;

  private ConditionType type;

  static {
    dangerLevel.put(ConditionType.CLEAR, 0);
    dangerLevel.put(ConditionType.FEW_CLOUDS, 1);
    dangerLevel.put(ConditionType.CLOUDS, 2);
    dangerLevel.put(ConditionType.MIST, 3);
    dangerLevel.put(ConditionType.SHOWER, 4);
    dangerLevel.put(ConditionType.RAIN, 5);
    dangerLevel.put(ConditionType.STORM, 6);
    dangerLevel.put(ConditionType.SNOW, 7);
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public double getTemp() {
    return temp;
  }

  public void setTemp(double temp) {
    this.temp = temp;
  }

  public double getTempMin() {
    return tempMin;
  }

  public void setTempMin(double tempMin) {
    this.tempMin = tempMin;
  }

  public double getTempMax() {
    return tempMax;
  }

  public void setTempMax(double tempMax) {
    this.tempMax = tempMax;
  }

  public ConditionType getType() {
    return type;
  }

  public void setType(ConditionType type) {
    this.type = type;
  }
}
