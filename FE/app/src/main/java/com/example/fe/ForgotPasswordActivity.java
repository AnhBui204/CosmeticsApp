package com.example.fe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnNextForgot;
    private ImageView ivBack;
    private ForgotPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnNextForgot = findViewById(R.id.btnNextForgot);
        ivBack = findViewById(R.id.ivBack);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        // Back navigation
        ivBack.setOnClickListener(v -> finish());

        // Handle reset request
        btnNextForgot.setOnClickListener(v -> handleResetPassword());
        viewModel.getForgotResponse().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                if (response.isSuccess()) {
                    // Chuyển sang VerificationCodeActivity
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerificationCodeActivity.class);
                    intent.putExtra("email", etForgotEmail.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }

    private void handleResetPassword() {
        String email = etForgotEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etForgotEmail.setError("Email cannot be empty");
            etForgotEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etForgotEmail.setError("Enter a valid email address");
            etForgotEmail.requestFocus();
            return;
        }

        viewModel.sendForgotPassword(email);
    }
}

