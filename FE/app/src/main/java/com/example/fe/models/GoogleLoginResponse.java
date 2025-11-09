package com.example.fe.models;

import com.example.fe.data.GoogleUserData;

public class GoogleLoginResponse {
    private boolean success;
    private String message;
    private boolean needRole;
    private GoogleUserData data; // tương tự UserData nhưng phoneNumber/wishlist optional

    public boolean isSuccess() { return success; }
    public boolean isNeedRole() { return needRole; }
    public String getMessage() { return message; }
    public GoogleUserData getData() { return data; }
}

