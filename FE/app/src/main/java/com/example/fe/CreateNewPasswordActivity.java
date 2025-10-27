package com.example.fe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private ImageView ivToggleNew, ivToggleConfirm;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

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

        btnConfirm.setOnClickListener(v -> validatePassword());
    }

    private void validatePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$")) {
            Toast.makeText(this,
                    "Password must be at least 8 characters, include uppercase, lowercase, number, and special character",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Your password has been changed")
                .setMessage("Welcome back! Discover now!")
                .setPositiveButton("Browse home", (dialog, which) -> finish())
                .show();
    }
}
