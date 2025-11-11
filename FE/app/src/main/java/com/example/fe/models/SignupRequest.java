package com.example.fe.models;

import android.location.Address;

import java.util.List;

public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber; // tùy chọn, nếu bạn muốn
    private String role;
    private List<Address> addresses;

    public SignupRequest(String fullName, String email, String password, String phoneNumber, String role, List<Address> addresses) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.addresses = addresses;
    }

    public static class Address {
        private String street;
        private String city;
        private String district;
        private String ward;
        private boolean isDefault;

        public Address(String street, String city, String district, String ward, boolean isDefault) {
            this.street = street;
            this.city = city;
            this.district = district;
            this.ward = ward;
            this.isDefault = isDefault;
        }

    }
}