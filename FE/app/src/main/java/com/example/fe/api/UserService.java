package com.example.fe.api;

import com.example.fe.data.UserData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface UserService {


    @PUT("api/users/me")
    Call<UserData> updateProfile(@Body Map<String, String> body);
    @PUT("api/users/change-password")
    Call<Void> changePassword(@Body Map<String, String> body);
}
