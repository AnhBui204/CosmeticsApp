package com.example.fe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fe.models.ForgotPasswordResponse;
import com.example.fe.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<ForgotPasswordResponse> forgotResponse = new MutableLiveData<>();

    public ForgotPasswordViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<ForgotPasswordResponse> getForgotResponse() {
        return forgotResponse;
    }

    public void sendForgotPassword(String email) {
        repository.forgotPassword(email).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    forgotResponse.postValue(response.body());
                } else {
                    String message = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            message = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ForgotPasswordResponse error = new ForgotPasswordResponse(false, message);
                    forgotResponse.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                ForgotPasswordResponse error = new ForgotPasswordResponse(false, t.getMessage());
                forgotResponse.postValue(error);
            }
        });
    }
}
