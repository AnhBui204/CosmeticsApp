package com.example.fe.models;

public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber; // tùy chọn, nếu bạn muốn
    private String role;
    public SignupRequest(String fullName, String email, String password,String phoneNumber,String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // getter & setter nếu cần
}