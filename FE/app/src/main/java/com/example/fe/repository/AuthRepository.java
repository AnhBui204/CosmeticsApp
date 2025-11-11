package com.example.fe.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.fe.api.ApiClient;
import com.example.fe.api.AuthService;
import com.example.fe.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class AuthRepository {

    private AuthService authService;

    public AuthRepository() {
        authService = ApiClient.getPublicClient().create(AuthService.class);
    }

    public AuthRepository(Context context) {
        authService = ApiClient.getAuthClient(context).create(AuthService.class);
    }

    public Call<SignupResponse> signup(String fullName, String email, String password,
                                       String phoneNumber, String role, List<SignupRequest.Address> addresses) {
        return authService.signup(new SignupRequest(fullName, email, password, phoneNumber, role, addresses));
    }


    public Call<LoginResponse> login(String email, String password) {
        return authService.login(new LoginRequest(email, password));
    }


    public Call<GoogleLoginResponse> googleLogin(String idToken, @Nullable String role) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken, role);
        return authService.googleLogin(request);
    }
    public Call<ForgotPasswordResponse> forgotPassword(String email) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        return authService.forgotPassword(body);
    }
    public Call<VerifyOTPResponse> verifyOTP(String email, String code) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("code", code);
        return authService.verifyOTP(body);
    }

    public Call<ResetPasswordResponse> resetPassword(String resetToken, String newPassword) {
        Map<String, String> body = new HashMap<>();
        body.put("resetToken", resetToken);
        body.put("newPassword", newPassword);
        return authService.resetPassword(body);
    }


}
