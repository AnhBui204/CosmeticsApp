package com.example.fe.api;

import com.example.fe.models.ForgotPasswordResponse;
import com.example.fe.models.GoogleLoginRequest;
import com.example.fe.models.GoogleLoginResponse;
import com.example.fe.models.LoginRequest;
import com.example.fe.models.LoginResponse;
import com.example.fe.models.ResetPasswordResponse;
import com.example.fe.models.SignupRequest;
import com.example.fe.models.SignupResponse;
import com.example.fe.models.VerifyOTPResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
      @POST("/api/auth/register")
    Call<SignupResponse> signup(@Body SignupRequest request);
    @POST("/api/auth/google-login")
    Call<GoogleLoginResponse> googleLogin(@Body GoogleLoginRequest request);

    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body Map<String, String> body);
    @POST("/api/auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body Map<String, String> body);
    @POST("/api/auth/verify-otp")
    Call<VerifyOTPResponse> verifyOTP(@Body Map<String, String> body);

}