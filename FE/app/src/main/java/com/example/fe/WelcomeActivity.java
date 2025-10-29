package com.example.fe;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> {
            // Ví dụ: chuyển sang MainActivity
            startActivity(new Intent(WelcomeActivity.this, IntroActivity.class));
        });
    }
}
