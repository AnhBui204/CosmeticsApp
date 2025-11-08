package com.example.fe.ui.auth.createNewPass;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe.R;
import com.example.fe.ui.home.HomeActivity;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{6,}$";

    private EditText etNewPassword, etConfirmPassword;
    private ImageView ivToggleNew, ivToggleConfirm;
    private Button btnConfirmPassword;
    private boolean isNewVisible = false, isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivToggleNew = findViewById(R.id.ivToggleNew);
        ivToggleConfirm = findViewById(R.id.ivToggleConfirm);
        btnConfirmPassword = findViewById(R.id.btnConfirmPassword);

        // Toggle password visibility
        ivToggleNew.setOnClickListener(v -> togglePasswordVisibility(etNewPassword, ivToggleNew, true));
        ivToggleConfirm.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivToggleConfirm, false));

        // Confirm password action
        btnConfirmPassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                etNewPassword.setError("Please enter password");
                etConfirmPassword.setError("Please confirm password");
                return;
            }

            if (!newPassword.matches(PASSWORD_PATTERN)) {
                etNewPassword.setError("Password must be at least 6 chars, include uppercase, lowercase, number & special character");
                etNewPassword.requestFocus();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            // TODO: call API to update password here

            // Show success modal bottom sheet
            showPasswordChangedDialog();
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, boolean isNew) {
        boolean isVisible = isNew ? isNewVisible : isConfirmVisible;

        if (isVisible) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.ic_eye);
        }

        if (isNew) isNewVisible = !isVisible;
        else isConfirmVisible = !isVisible;

        editText.setSelection(editText.getText().length());
    }

    private void showPasswordChangedDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_password_changed);

        // Setup button click
        Button btnBrowseHome = dialog.findViewById(R.id.btnBrowseHome);
        btnBrowseHome.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate to home
            startActivity(new Intent(CreateNewPasswordActivity.this, HomeActivity.class));
            finish();
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }

        dialog.show();
    }
}
