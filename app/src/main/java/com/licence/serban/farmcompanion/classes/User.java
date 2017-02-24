package com.licence.serban.farmcompanion.classes;

/**
 * Created by Serban on 16/02/2017.
 */

public class User {
    private String name;
    private String companyName;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
