package com.example.fe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class VerificationCodeActivity extends AppCompatActivity {

    private EditText etCode1, etCode2, etCode3, etCode4;
    private Button btnNextCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        btnNextCode = findViewById(R.id.btnNextCode);

        setupCodeAutoMove();

        btnNextCode.setOnClickListener(v -> {
            String otp = etCode1.getText().toString().trim()
                    + etCode2.getText().toString().trim()
                    + etCode3.getText().toString().trim()
                    + etCode4.getText().toString().trim();

            if (otp.length() != 4) {
                Toast.makeText(VerificationCodeActivity.this, "Please enter 4-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = getIntent().getStringExtra("email"); // email từ màn ForgotPassword

            // Gọi API verify OTP
            ResetPasswordViewModel viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
            viewModel.verifyOTP(email, otp);

            viewModel.getVerifyResponse().observe(this, response -> {
                if (response.isSuccess()) {
                    // Lấy resetToken từ backend
                    String resetToken = response.getResetToken();
                    Log.d("VerificationCode", "Received resetToken: " + resetToken);
                    Toast.makeText(this, "ResetToken: " + resetToken, Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(VerificationCodeActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("resetToken", resetToken);
                    startActivity(intent);
                } else {
                    Toast.makeText(VerificationCodeActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

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
}
