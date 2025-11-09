package com.example.fe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private ImageView ivToggleNew, ivToggleConfirm;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private ResetPasswordViewModel viewModel;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        // Nhận email + OTP từ màn verification
        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");
        String resetToken = getIntent().getStringExtra("resetToken");
        android.util.Log.d("ResetPasswordActivity", "Received resetToken: " + resetToken);
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivToggleNew = findViewById(R.id.ivToggleNew);
        ivToggleConfirm = findViewById(R.id.ivToggleConfirm);
        Button btnConfirm = findViewById(R.id.btnConfirmPassword);

        // Toggle show/hide new password
        ivToggleNew.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            if (isNewPasswordVisible) {
                etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleNew.setImageResource(R.drawable.ic_eye_off);
            } else {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleNew.setImageResource(R.drawable.ic_eye);
            }
            etNewPassword.setSelection(etNewPassword.getText().length());
        });

        // Toggle show/hide confirm password
        ivToggleConfirm.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirm.setImageResource(R.drawable.ic_eye_off);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirm.setImageResource(R.drawable.ic_eye);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // Observe response từ backend
        viewModel.getResetResponse().observe(this, response -> {
            if (response.isSuccess()) {
                new AlertDialog.Builder(this)
                        .setTitle("Password Changed")
                        .setMessage("Go back to login")
                        .setPositiveButton("Login", (dialog, which) -> {
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }).show();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btnConfirm.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validatePasswordFields(newPassword, confirmPassword)) {
                viewModel.resetPassword(resetToken, newPassword);
            }
        });

    }

    private boolean validatePasswordFields(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$")) {
            Toast.makeText(this,
                    "Password must be at least 8 characters, include uppercase, lowercase, number, and special character",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
