package com.example.fe.models;

public class ResetPasswordResponse {
    private boolean success;
    private String message;

    public ResetPasswordResponse() {}

    public ResetPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
