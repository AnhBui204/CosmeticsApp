package com.example.fe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fe.R;
import com.example.fe.data.GoogleUserData;
import com.example.fe.ui.home.HomeActivity;
import com.example.fe.utils.SessionManager;
import com.example.fe.data.UserData;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private String googleIdToken;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgotPassword;
    private LoginViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ImageView btnGoogle = findViewById(R.id.btnGoogleLogin);
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        tvSignup = findViewById(R.id.tvSignupLink);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(this);

        // ---------------- Google Sign-In config ----------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // --------------------------------------------------------

        btnLogin.setOnClickListener(v -> handleLogin());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // Email/password login observer
        viewModel.getLoginResponse().observe(this, response -> {
            if (response != null) {
                if (response.isSuccess() && response.getData() != null) {
                    UserData user = response.getData();
                    sessionManager.saveUser(user);
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    navigateByRole(user.getRole());
                    finish();
                } else {
                    Toast.makeText(this, "Login Failed: " + response.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Login Failed: null response", Toast.LENGTH_LONG).show();
            }
        });

// Google login observer
        viewModel.getGoogleLoginResponse().observe(this, response -> {
            if (response != null) {
                if (response.isSuccess() && response.getData() != null) {
                    UserData user = new UserData(response.getData());

                    sessionManager.saveUser(user);
                    Toast.makeText(this, "Google Login Successful!", Toast.LENGTH_SHORT).show();
                    navigateByRole(user.getRole());
                    finish();
                } else if (response.isNeedRole()) {
                    // Show dialog chọn role
                    showRoleSelectionDialog(response.getData());
                } else {
                    Toast.makeText(this, "Google Login Failed: " + response.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Google Login Failed: null response", Toast.LENGTH_LONG).show();
            }
        });




    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        viewModel.login(email, password);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            googleIdToken = account.getIdToken();
            Log.d("GoogleLogin", "idToken: " + googleIdToken);
            if (googleIdToken != null) {
                viewModel.googleLogin(googleIdToken);
            }else {
                Toast.makeText(this, "idToken null, check Client ID", Toast.LENGTH_LONG).show();
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
    private void showRoleSelectionDialog(GoogleUserData data) {
        String[] roles = {"Customer", "Seller"};
        new AlertDialog.Builder(this)
                .setTitle("Choose your role")
                .setSingleChoiceItems(roles, -1, (dialog, which) -> {
                    String selectedRole = which == 0 ? "customer" : "seller";
                    // Gọi API với token + role
                    viewModel.googleLoginWithRole(googleIdToken, selectedRole);
                    dialog.dismiss();
                })
                .show();
    }

    private void navigateByRole(String role) {
        if ("seller".equalsIgnoreCase(role)) {
//            startActivity(new Intent(this, SellerDashboardActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }


}
