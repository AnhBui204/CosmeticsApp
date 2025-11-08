package com.example.fe.ui.auth.signup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fe.R;
import com.example.fe.ui.auth.verification.VerificationCodeActivity;

public class SignupActivity extends AppCompatActivity {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{6,}$";
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Sign Up Button Click
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // Navigate to Verification Code screen
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, VerificationCodeActivity.class);
                intent.putExtra("origin", "signup");
                startActivity(intent);
            }
        });

        // Toggle password visibility
        etPassword.setOnTouchListener((v, event) -> {
            if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[2].getBounds().width())) {
                togglePasswordVisibility(etPassword);
                return true;
            }
            return false;
        });

        etConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getRawX() >= (etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawables()[2].getBounds().width())) {
                togglePasswordVisibility(etConfirmPassword);
                return true;
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText editText) {
        if (editText.getInputType() ==
                (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

            // Show password
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
        } else {
            // Hide password
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
        }

        // Move cursor to end after toggling
        editText.setSelection(editText.getText().length());
    }


    // Sign Up Validation Logic
    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // --- Validation ---
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

        if (!password.matches(PASSWORD_PATTERN)) {
            etPassword.setError("Password must be at least 6 chars, include uppercase, lowercase, number & special character");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // --- Success ---
        Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();

        // Send data to your API or Firebase here
        // registerUser(name, email, password);

        // navigate to email verification screen
        Intent intent = new Intent(SignupActivity.this, VerificationCodeActivity.class);
        intent.putExtra("origin", "signup");
        startActivity(intent);
        finish();
    }
}
