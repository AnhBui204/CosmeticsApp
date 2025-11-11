package com.example.fe.models;

public class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;

    public UpdateProfileRequest(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;

    }
}
