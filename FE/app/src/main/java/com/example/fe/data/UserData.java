package com.example.fe.data;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("_id")
    private String _id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;
    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("token")
    private String token;

    private String loginProvider;
    public UserData() {
        // constructor mặc định
    }

    public UserData(GoogleUserData googleUser) {
        this._id = googleUser.get_id();
        this.fullName = googleUser.getFullName();
        this.email = googleUser.getEmail();
        this.phoneNumber=googleUser.getPhoneNumber();
        this.role = googleUser.getRole();
        this.token = googleUser.getToken();
        this.loginProvider = googleUser.getLoginProvider();
    }


    public UserData(String _id, String fullName, String email, String role, String token) {
        this._id = _id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
    }
    // Getter
    public String getId() { return _id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public String getPhoneNumber(){return phoneNumber;}
    public String getLoginProvider() { return loginProvider; }
    public void setLoginProvider(String loginProvider) { this.loginProvider = loginProvider; }
    // Setter
    public void setId(String id) { this._id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber){this.phoneNumber=phoneNumber;}
    public void setRole(String role) { this.role = role; }
    public void setToken(String token) { this.token = token; }
}
