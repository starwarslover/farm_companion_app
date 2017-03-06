package com.licence.serban.farmcompanion.classes;

import java.util.Date;

/**
 * Created by Serban on 06.03.2017.
 */

public class Employee {
    private String id;
    private String name;
    private String personalIdNumber;
    private String idNumber;
    private Date startDate;
    private String position;
    private double baseSalary;
    private String contractNumber;
    private String accountEmail;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
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

    public Employee() {
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
}
