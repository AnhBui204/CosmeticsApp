package com.example.fe.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.fe.R;

public class ProfileSettingActivity extends AppCompatActivity {

    private ImageView ivBack, imgAvatar, ivCamera;
    private EditText etFirstName, etLastName, etEmail, etGender, etPhone;
    private Button btnSaveChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile_setting);

        // Khởi tạo views
        ivBack = findViewById(R.id.ivBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        ivCamera = findViewById(R.id.ivCamera);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etGender = findViewById(R.id.etGender);
        etPhone = findViewById(R.id.etPhone);
        btnSaveChange = findViewById(R.id.btnSaveChange);

        // Xử lý nút Back → chỉ cần finish()
        ivBack.setOnClickListener(v -> finish());

        // Xử lý click vào camera icon để đổi ảnh đại diện
        ivCamera.setOnClickListener(v ->
                Toast.makeText(ProfileSettingActivity.this, "Change profile picture", Toast.LENGTH_SHORT).show()
        );

        // Xử lý click vào Gender để chọn giới tính
        etGender.setOnClickListener(v -> showGenderDialog());

        // Xử lý nút Save Change
        btnSaveChange.setOnClickListener(v -> saveProfileChanges());
    }

    // Hiển thị dialog chọn giới tính
    private void showGenderDialog() {
        String[] genders = {"Male", "Female", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(genders, (dialog, which) -> etGender.setText(genders[which]))
                .show();
    }

    // Lưu thay đổi thông tin profile
    private void saveProfileChanges() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return;
        }

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }
}
