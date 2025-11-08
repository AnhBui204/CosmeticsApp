package com.example.fe.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe.R;

public class VerificationCodeActivity extends AppCompatActivity {

    private EditText etCode1, etCode2, etCode3, etCode4;
    private Button btnNextCode;
    private String origin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        btnNextCode = findViewById(R.id.btnNextCode);

        // read where user came from (signup/forgot password)
        origin = getIntent().getStringExtra("origin");

        setupCodeAutoMove();

        btnNextCode.setOnClickListener(v -> verifyCode());
    }

    private void setupCodeAutoMove() {
        EditText[] codeFields = {etCode1, etCode2, etCode3, etCode4};

        for (int i = 0; i < codeFields.length; i++) {
            final int index = i;
            codeFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Move to next box when a digit is entered
                    if (s.length() == 1 && index < codeFields.length - 1) {
                        codeFields[index + 1].requestFocus();
                    }
                    // Move back if user deletes
                    else if (s.length() == 0 && index > 0) {
                        codeFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void verifyCode() {
        String code = etCode1.getText().toString() +
                etCode2.getText().toString() +
                etCode3.getText().toString() +
                etCode4.getText().toString();

        if (code.length() != 4) {
            Toast.makeText(this, "Enter the 4-digit code", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("signup".equals(origin)) {
            // show success dialog instead of going directly to homepage
            showSuccessDialog();
        }
        else if ("forgot".equals(origin)) {
            // navigate to CreateNewPasswordActivity for forgot password
            startActivity(new Intent(this, CreateNewPasswordActivity.class));
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signup_success);

        // Setup button click
        Button btnBrowseHome = dialog.findViewById(R.id.btnBrowseHome);
        btnBrowseHome.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate to home
            startActivity(new Intent(VerificationCodeActivity.this, HomePageActivity.class));
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
