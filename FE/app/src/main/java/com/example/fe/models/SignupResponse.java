package com.example.fe.models;

import java.util.List;

public class SignupResponse {
    private boolean success;
    private String message;
    private SignupData data;

    // Default constructor (bắt buộc cho Gson)
    public SignupResponse() { }

    // Constructor tạo lỗi thủ công
    public SignupResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter và Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public SignupData getData() { return data; }
    public void setData(SignupData data) { this.data = data; }

    public static class SignupData {
        private String _id;
        private String fullName;
        private String email;
        private String role;
        private String token;
        private List<Address> addresses; // thêm

        public static class Address {
            private String street;
            private String city;
            private String district;
            private String ward;
            private boolean isDefault;

            public Address() { } // default constructor

            // Getter & Setter
            public String getStreet() { return street; }
            public void setStreet(String street) { this.street = street; }

            public String getCity() { return city; }
            public void setCity(String city) { this.city = city; }

            public String getDistrict() { return district; }
            public void setDistrict(String district) { this.district = district; }

            public String getWard() { return ward; }
            public void setWard(String ward) { this.ward = ward; }

            public boolean isDefault() { return isDefault; }
            public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
        }

        // Default constructor
        public SignupData() { }

        // Getter & Setter cho addresses
        public List<Address> getAddresses() { return addresses; }
        public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    }

}
