package com.example.fe.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fe.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Initialize UI
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignupLink = findViewById(R.id.tvSignupLink);

        // 🔹 Login button click
        btnLogin.setOnClickListener(v -> handleLogin());

        // 🔹 Navigate to Signup
        tvSignupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        // 🔹 Forgot password
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );
    }

    // 🔹 Validation logic
    private void handleLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("Valid email required");
            etLoginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etLoginPassword.setError("Password required");
            etLoginPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etLoginPassword.setError("Password must be at least 6 characters");
            etLoginPassword.requestFocus();
            return;
        }

        // ✅ Success
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

        // 🔹 Navigate to HomePage
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}
