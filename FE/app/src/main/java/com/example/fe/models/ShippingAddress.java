package com.example.fe.models;

public class ShippingAddress {
    private String fullAddress;
    // bạn có thể thêm city, country nếu backend trả
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
}