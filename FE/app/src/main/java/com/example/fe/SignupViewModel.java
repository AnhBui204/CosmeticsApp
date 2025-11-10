package com.example.fe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fe.models.SignupRequest;
import com.example.fe.models.SignupResponse;
import com.example.fe.repository.AuthRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupViewModel extends ViewModel {

    private AuthRepository repository;
    private MutableLiveData<SignupResponse> signupResponse = new MutableLiveData<>();

    public SignupViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<SignupResponse> getSignupResponse() {
        return signupResponse;
    }

    public void signup(String fullName, String email, String password, String phoneNumber, String role,
                       String street, String city, String district, String ward) {
        // Tạo Address mặc định
        SignupRequest.Address address = new SignupRequest.Address(street, city, district, ward, true);
        List<SignupRequest.Address> addresses = new ArrayList<>();
        addresses.add(address);

        repository.signup(fullName, email, password, phoneNumber, role, addresses)
                .enqueue(new Callback<SignupResponse>() {
                    @Override
                    public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            signupResponse.postValue(response.body());
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String errorString = response.errorBody().string();
                                    JSONObject jObj = new JSONObject(errorString);
                                    String message = jObj.has("message") ? jObj.getString("message") : "Unknown error";
                                    signupResponse.postValue(new SignupResponse(false, message));
                                } else {
                                    signupResponse.postValue(new SignupResponse(false, "Unknown error"));
                                }
                            } catch (Exception e) {
                                signupResponse.postValue(new SignupResponse(false, "Parsing error"));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignupResponse> call, Throwable t) {
                        signupResponse.postValue(new SignupResponse(false, t.getMessage()));
                    }
                });
    }

}
