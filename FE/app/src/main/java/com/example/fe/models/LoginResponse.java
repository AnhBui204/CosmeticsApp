package com.example.fe.models;

import com.example.fe.data.UserData;

public class LoginResponse {
    private boolean success;
    private String message;
    private UserData data; //  user info + token
    public LoginResponse(boolean success, String message, UserData data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    // Getter
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserData getData() { return data; }


}
