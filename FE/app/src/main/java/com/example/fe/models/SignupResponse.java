package com.example.fe.models;

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

        // Default constructor
        public SignupData() { }

        // Getter và Setter
        public String getId() { return _id; }
        public void setId(String _id) { this._id = _id; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
