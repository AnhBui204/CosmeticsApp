package com.example.fe.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fe.R;
import com.example.fe.data.UserData;
import com.example.fe.utils.SessionManager;

public class ProfileSettingActivity extends AppCompatActivity {

    private ImageView ivBack, imgAvatar, ivCamera;
    private EditText etFullName, etEmail, etPhone;
    private EditText etOldPassword, etNewPassword;
    private LinearLayout layoutChangePassword;
    private Button btnSaveChange;
    private Button btnShowChangePassword;
    private SessionManager sessionManager;
    private UserData currentUser;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile_setting);

        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUser();
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        bindUserData();
        observeViewModel();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        ivCamera = findViewById(R.id.ivCamera);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);

        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        btnSaveChange = findViewById(R.id.btnSaveChange);
        btnShowChangePassword = findViewById(R.id.btnShowChangePassword);

        // Mặc định ẩn layout đổi mật khẩu
        layoutChangePassword.setVisibility(View.GONE);

        if ("google".equals(currentUser.getLoginProvider())) {
            btnShowChangePassword.setVisibility(View.GONE); // Google login thì ẩn nút
            layoutChangePassword.setVisibility(View.GONE);
        } else {
            btnShowChangePassword.setVisibility(View.VISIBLE); // Email/password thì hiện nút
        }
    }


    private void bindUserData() {
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivCamera.setOnClickListener(v ->
                Toast.makeText(this, "Change profile picture", Toast.LENGTH_SHORT).show()
        );
        btnShowChangePassword.setOnClickListener(v -> {
            if (layoutChangePassword.getVisibility() == View.GONE) {
                layoutChangePassword.setVisibility(View.VISIBLE);
                btnShowChangePassword.setText("Cancel Change Password");
            } else {
                layoutChangePassword.setVisibility(View.GONE);
                btnShowChangePassword.setText("Change Password");
                etOldPassword.setText("");
                etNewPassword.setText("");
            }
        });

        btnSaveChange.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!validateInputs(fullName, phone)) return;

            if (layoutChangePassword.getVisibility() == View.VISIBLE) {
                String oldPassword = etOldPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đổi mật khẩu trước
                viewModel.changePassword(oldPassword, newPassword, result -> {
                    if (!result) {
                        Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Nếu đổi mật khẩu thành công, update profile
                    viewModel.updateProfile(fullName, phone);
                });
            } else {
                // Chỉ update profile
                viewModel.updateProfile(fullName, phone);
            }
        });
    }

    private boolean validateInputs(String fullName, String phone) {
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return false;
        }
        return true;
    }

    private void observeViewModel() {
        viewModel.getUpdateResponse().observe(this, user -> {
            if (user != null) {
                user.setLoginProvider(currentUser.getLoginProvider());
                sessionManager.saveUser(user);
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
