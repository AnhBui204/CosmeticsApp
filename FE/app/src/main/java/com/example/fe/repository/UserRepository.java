package com.example.fe.repository;

import android.content.Context;

import com.example.fe.api.ApiClient;
import com.example.fe.api.UserService;
import com.example.fe.data.UserData;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserService userService;

    public UserRepository(Context context) {
        userService = ApiClient.getAuthClient(context).create(UserService.class);
    }


    public void updateProfile(String fullName, String phoneNumber, UpdateCallback callback) {
        Map<String, String> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("phoneNumber", phoneNumber);

        userService.updateProfile(body).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Update failed"));
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                callback.onError(t);
            }
        });


    }
    public void changePassword(String oldPassword, String newPassword, ChangePassCallback callback) {
        Map<String, String> body = new HashMap<>();
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);

        userService.changePassword(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }
    public interface UpdateCallback {
        void onSuccess(UserData user);
        void onError(Throwable t);
    }


    public interface ChangePassCallback {
        void onResult(boolean success);
    }
}
