package com.example.fe;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etForgotEmail.setError("Enter a valid email");
            etForgotEmail.requestFocus();
            return;
        }

        // You can integrate Firebase reset password here
        Toast.makeText(this, "Password reset link sent to " + email, Toast.LENGTH_LONG).show();

        // Example for Firebase:
        // FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        //     .addOnSuccessListener(aVoid -> {
        //         Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show();
        //         finish();
        //     })
        //     .addOnFailureListener(e ->
        //         Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

