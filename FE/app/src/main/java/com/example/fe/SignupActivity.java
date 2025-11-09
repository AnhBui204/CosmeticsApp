package com.example.fe;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fe.R;
import com.example.fe.LoginActivity;
import com.example.fe.ui.home.HomeActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLoginLink;
    private SignupViewModel viewModel;
    private EditText etPhone;
    private RadioGroup rgRole;
    private RadioButton rbCustomer, rbSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        rgRole = findViewById(R.id.rgRole);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbSeller = findViewById(R.id.rbSeller);
        viewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        btnSignUp.setOnClickListener(v -> handleSignUp());

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        viewModel.getSignupResponse().observe(this, response -> {
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String phoneNumber = etPhone.getText().toString().trim();
        String role = rbCustomer.isChecked() ? "customer" : "seller";

        // Validate trước
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        if (!password.matches(passwordPattern)) {
            etPassword.setError("Password must be at least 8 characters, include uppercase, lowercase, number, and special character");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        // Gọi signup duy nhất sau khi validate xong
        viewModel.signup(name, email, password, phoneNumber, role);
    }
}
