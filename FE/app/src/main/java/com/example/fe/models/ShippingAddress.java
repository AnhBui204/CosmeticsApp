// com/example/fe/models/ShippingAddress.java
package com.example.fe.models;

import com.google.gson.annotations.SerializedName;

public class ShippingAddress {
    @SerializedName("street")
    private String street;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("phone")
    private String phone;

    @SerializedName("fullName")
    private String fullName;

    // Getters
    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }

    public String getFullName() {
        return fullName;
    }

    // Setters
    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Hàm toString() đơn giản để hiển thị
    @Override
    public String toString() {
        return fullName + ", " + phone + "\n" + street + ", " + city;
    }
}