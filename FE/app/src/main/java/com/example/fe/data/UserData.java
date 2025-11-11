package com.example.fe.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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
    @SerializedName("addresses")
    private List<Address> addresses;

    private String loginProvider;
    public UserData() {
        // constructor mặc định
    }
    public static class Address {
        private String street;
        private String ward;
        private String district;
        private String city;
        private boolean isDefault;

        // Getter & Setter
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getWard() { return ward; }
        public void setWard(String ward) { this.ward = ward; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean aDefault) { isDefault = aDefault; }
    }
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    public UserData(GoogleUserData googleUser) {
        this._id = googleUser.get_id();
        this.fullName = googleUser.getFullName();
        this.email = googleUser.getEmail();
        this.phoneNumber = googleUser.getPhoneNumber();
        this.role = googleUser.getRole();
        this.token = googleUser.getToken();
        this.loginProvider = googleUser.getLoginProvider();
        this.addresses = googleUser.getAddresses() != null ? googleUser.getAddresses() : new ArrayList<>();
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
