package com.example.fe.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fe.data.UserData;
import com.example.fe.repository.UserRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<UserData> updateResponse = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void updateProfile(String fullName, String phoneNumber) {
        userRepository.updateProfile(fullName, phoneNumber, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess(UserData user) {
                updateResponse.postValue(user);
            }

            @Override
            public void onError(Throwable t) {
                updateResponse.postValue(null);
            }
        });
    }
    public void changePassword(String oldPassword, String newPassword, UserRepository.ChangePassCallback callback) {
        userRepository.changePassword(oldPassword, newPassword, callback);
    }
    public LiveData<UserData> getUpdateResponse() {
        return updateResponse;
    }
}
