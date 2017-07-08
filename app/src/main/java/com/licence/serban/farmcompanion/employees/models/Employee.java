package com.licence.serban.farmcompanion.employees.models;

/**
 * Created by Serban on 06.03.2017.
 */

public class Employee {
  private String id;
  private String name;
  private String personalIdNumber;
  private String idNumber;
  private long startDate;
  private String position;
  private double baseSalary;
  private String contractNumber;
  private String accountEmail;
  private long createdAt;
  private EEmployeeState state;

  public Employee() {
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getAccountEmail() {
    return accountEmail;
  }

  public void setAccountEmail(String accountEmail) {
    this.accountEmail = accountEmail;
  }

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

  public String getPersonalIdNumber() {
    return personalIdNumber;
  }

  public void setPersonalIdNumber(String personalIdNumber) {
    this.personalIdNumber = personalIdNumber;
  }

  public String getIdNumber() {
    return idNumber;
  }

  public void setIdNumber(String idNumber) {
    this.idNumber = idNumber;
  }

  public long getStartDate() {
    return startDate;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public double getBaseSalary() {
    return baseSalary;
  }

  public void setBaseSalary(double baseSalary) {
    this.baseSalary = baseSalary;
  }

  public String getContractNumber() {
    return contractNumber;
  }

  public void setContractNumber(String contractNumber) {
    this.contractNumber = contractNumber;
  }

  @Override
  public String toString() {
    return "Employee{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", personalIdNumber='" + personalIdNumber + '\'' +
            ", idNumber='" + idNumber + '\'' +
            ", startDate=" + startDate +
            ", position='" + position + '\'' +
            ", baseSalary=" + baseSalary +
            ", contractNumber='" + contractNumber + '\'' +
            '}';
  }

  public EEmployeeState getState() {
    return state;
  }

  public void setState(EEmployeeState state) {
    this.state = state;
  }
}
