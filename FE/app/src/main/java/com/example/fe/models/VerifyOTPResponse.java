package com.example.fe.models;

public class VerifyOTPResponse {
    private boolean success;
    private String message;
    private String resetToken; // token trả về nếu OTP hợp lệ

    public VerifyOTPResponse() {}

    public VerifyOTPResponse(boolean success, String message, String resetToken) {
        this.success = success;
        this.message = message;
        this.resetToken = resetToken;
    }

    // getter và setter
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getResetToken() { return resetToken; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
}
