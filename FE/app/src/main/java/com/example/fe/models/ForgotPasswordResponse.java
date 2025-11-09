package com.example.fe.models;

public class ForgotPasswordResponse {
    private boolean success;
    private String message;

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
