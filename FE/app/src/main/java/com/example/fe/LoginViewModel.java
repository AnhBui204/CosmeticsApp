package com.example.fe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fe.models.GoogleLoginResponse;
import com.example.fe.models.LoginResponse;
import com.example.fe.repository.AuthRepository;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    private final MutableLiveData<GoogleLoginResponse> googleLoginResponse = new MutableLiveData<>();

    public LoginViewModel() {
        repository = new AuthRepository();
    }

    // --- Email/password login LiveData ---
    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponse;
    }

    // --- Google login LiveData ---
    public LiveData<GoogleLoginResponse> getGoogleLoginResponse() {
        return googleLoginResponse;
    }

    // --- Email/password login ---
    public void login(String email, String password) {
        repository.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.postValue(response.body());
                }else {
                    String errorMsg = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            String json = response.errorBody().string();
                            JSONObject obj = new JSONObject(json);
                            if (obj.has("message")) {
                                errorMsg = obj.getString("message");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LoginResponse errorResponse = new LoginResponse(false, errorMsg, null);
                    loginResponse.postValue(errorResponse);
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                LoginResponse errorResponse = new LoginResponse(false, t.getMessage(), null);
                loginResponse.postValue(errorResponse);
            }
        });
    }



    // --- Google login ---
    // Khi login Google bình thường (chưa chọn role)
    // Google login lần đầu (chưa chọn role)
    public void googleLogin(String idToken) {
        googleLogin(idToken, null);
    }

    // Google login với role
    public void googleLogin(String idToken, String role) {
        repository.googleLogin(idToken, role).enqueue(new Callback<GoogleLoginResponse>() {
            @Override
            public void onResponse(Call<GoogleLoginResponse> call, Response<GoogleLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    googleLoginResponse.postValue(response.body());
                } else {
                    googleLoginResponse.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<GoogleLoginResponse> call, Throwable t) {
                googleLoginResponse.postValue(null);
            }
        });
    }

    // Cho tiện, tạo alias cho role selection
    public void googleLoginWithRole(String idToken, String role) {
        googleLogin(idToken, role);
    }



}
