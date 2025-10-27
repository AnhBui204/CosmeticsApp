package com.example.fe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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

        btnNextCode.setOnClickListener(v ->
                startActivity(new Intent(VerificationCodeActivity.this, CreateNewPasswordActivity.class))
        );
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
