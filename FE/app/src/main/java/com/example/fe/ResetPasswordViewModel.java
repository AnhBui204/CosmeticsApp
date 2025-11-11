package com.example.fe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fe.models.ForgotPasswordResponse;
import com.example.fe.models.VerifyOTPResponse;
import com.example.fe.models.ResetPasswordResponse;
import com.example.fe.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();
    private final MutableLiveData<ForgotPasswordResponse> forgotResponse = new MutableLiveData<>();
    private final MutableLiveData<VerifyOTPResponse> verifyResponse = new MutableLiveData<>();
    private final MutableLiveData<ResetPasswordResponse> resetResponse = new MutableLiveData<>();

    public LiveData<ForgotPasswordResponse> getForgotResponse() { return forgotResponse; }
    public LiveData<VerifyOTPResponse> getVerifyResponse() { return verifyResponse; }
    public LiveData<ResetPasswordResponse> getResetResponse() { return resetResponse; }

    public void forgotPassword(String email) {
        repository.forgotPassword(email).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                forgotResponse.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                forgotResponse.postValue(new ForgotPasswordResponse(false, t.getMessage()));
            }
        });
    }

    public void verifyOTP(String email, String code) {
        repository.verifyOTP(email, code).enqueue(new Callback<VerifyOTPResponse>() {
            @Override
            public void onResponse(Call<VerifyOTPResponse> call, Response<VerifyOTPResponse> response) {
                verifyResponse.postValue(response.body());
            }
            @Override
            public void onFailure(Call<VerifyOTPResponse> call, Throwable t) {
                verifyResponse.postValue(new VerifyOTPResponse(false, t.getMessage(), null));
            }
        });
    }

    public void resetPassword(String resetToken, String newPassword) {
        repository.resetPassword(resetToken, newPassword).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                resetResponse.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                resetResponse.postValue(new ResetPasswordResponse(false, t.getMessage()));
            }
        });
    }
}
