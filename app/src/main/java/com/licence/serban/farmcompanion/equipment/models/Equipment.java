package com.licence.serban.farmcompanion.equipment.models;

import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.activities.MainActivity;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;

/**
 * Created by Serban on 25.04.2017.
 */

public class Equipment {
  private String id;
  private String type;
  private String manufacturer;
  private String model;
  private int manufacturingYear;
  private long purchaseDate;
  private String serialNumber;
  private String plateNumber;
  private double purchasePrice;
  private String ownership;
  private EquipmentState state;
  private String engineType;
  private int engineCapacity;
  private String transmissionType;
  private long createdAt;

  public Equipment() {
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTransmissionType() {
    return transmissionType;
  }

  public void setTransmissionType(String transmissionType) {
    this.transmissionType = transmissionType;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public int getManufacturingYear() {
    return manufacturingYear;
  }

  public void setManufacturingYear(int manufacturingYear) {
    this.manufacturingYear = manufacturingYear;
  }

  public long getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(long purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getPlateNumber() {
    return plateNumber;
  }

  public void setPlateNumber(String plateNumber) {
    this.plateNumber = plateNumber;
  }

  public double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public String getOwnership() {
    return ownership;
  }

  public void setOwnership(String ownership) {
    this.ownership = ownership;
  }

  public EquipmentState getState() {
    return state;
  }

  public void setState(EquipmentState state) {
    this.state = state;
  }

  public String getEngineType() {
    return engineType;
  }

  public void setEngineType(String engineType) {
    this.engineType = engineType;
  }

  public int getEngineCapacity() {
    return engineCapacity;
  }

  public void setEngineCapacity(int engineCapacity) {
    this.engineCapacity = engineCapacity;
  }

  public void onTaskStatusChanged(EquipmentState state) {
    this.state = state;
    FirebaseDatabase.getInstance().getReference()
            .child(EquipmentDatabaseAdapter.DB_EQUIPMENTS)
            .child(MainActivity.adminID).child(id)
            .child("state").setValue(this.state);
  }

}
