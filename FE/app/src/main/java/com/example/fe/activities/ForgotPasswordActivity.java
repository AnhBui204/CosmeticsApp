package com.example.fe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fe.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnNextForgot;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnNextForgot = findViewById(R.id.btnNextForgot);
        ivBack = findViewById(R.id.ivBack);

        // Back navigation
        ivBack.setOnClickListener(v -> finish());

        // Handle reset request
        btnNextForgot.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        String email = etForgotEmail.getText().toString().trim();

        // Validate email format
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

        // Success — show notification
        Toast.makeText(this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show();

        // Navigate to verification code screen
        Intent intent = new Intent(ForgotPasswordActivity.this, VerificationCodeActivity.class);
        intent.putExtra("origin", "forgot");
        startActivity(intent);
    }
}

