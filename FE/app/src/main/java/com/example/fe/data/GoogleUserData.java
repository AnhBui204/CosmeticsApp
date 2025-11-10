package com.example.fe.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GoogleUserData {
    @SerializedName("_id")
    private String _id;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private String loginProvider = "google";
    private String phoneNumber; // optional
    private List<String> wishlist; // optional
    private List<UserData.Address> addresses;
    // getters + setters
    public String get_id() { return _id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber(){return phoneNumber;}
    public String getRole() { return role; }
    public String getToken() { return token; }
    public String getLoginProvider() { return loginProvider; }
    public List<UserData.Address> getAddresses() { return addresses; }
    public void setAddresses(List<UserData.Address> addresses) { this.addresses = addresses; }
    public void setLoginProvider(String loginProvider) { this.loginProvider = loginProvider; }
    public void set_id(String _id) { this._id = _id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber){this.phoneNumber=phoneNumber;}
    public void setRole(String role) { this.role = role; }
    public void setToken(String token) { this.token = token; }
}
