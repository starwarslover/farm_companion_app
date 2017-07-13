package com.licence.serban.farmcompanion.consumables.models;

import com.licence.serban.farmcompanion.misc.ConsumableEnum;

/**
 * Created by Serban on 04.07.2017.
 */

public class Consumable {
  private String id;
  private ConsumableEnum type;
  private String name;
  private double amount;
  private double purchasePrice;
  private String purchasedFrom;
  private long purchaseDate;
  private String notes;
  private long createdAt;

  public Consumable() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ConsumableEnum getType() {
    return type;
  }

  public void setType(ConsumableEnum type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public String getPurchasedFrom() {
    return purchasedFrom;
  }

  public void setPurchasedFrom(String purchasedFrom) {
    this.purchasedFrom = purchasedFrom;
  }

  public long getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(long purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getUM() {
    if (this.getType() == ConsumableEnum.IERBICIDE || this.getType() == ConsumableEnum.COMBUSTIBIL)
      return "L";
    return "Kg";
  }
}
