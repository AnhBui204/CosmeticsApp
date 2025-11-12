package com.example.fe.ui.voucher;


import com.example.fe.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe.models.VoucherItem;


public class AddVoucherActivity extends AppCompatActivity {

    private EditText etCode, etTitle, etDescription, etEndDate, etStatus;
    private Button btnSave;
    private VoucherItem editVoucher;
    private int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voucher);

        etCode = findViewById(R.id.etCode);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etEndDate = findViewById(R.id.etEndDate);
        etStatus = findViewById(R.id.etStatus);
        btnSave = findViewById(R.id.btnSaveVoucher);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        if (intent.hasExtra("editVoucher")) {
            editVoucher = (VoucherItem) intent.getSerializableExtra("editVoucher");
            editPosition = intent.getIntExtra("position", -1);

            etCode.setText(editVoucher.getCode());
            etTitle.setText(editVoucher.getTitle());
            etDescription.setText(editVoucher.getDescription());
            etEndDate.setText(editVoucher.getEndDate());
            etStatus.setText(editVoucher.getStatus());
        }

        btnSave.setOnClickListener(v -> {
            VoucherItem newVoucher = new VoucherItem(
                    etCode.getText().toString(),
                    etTitle.getText().toString(),
                    etDescription.getText().toString(),
                    etEndDate.getText().toString(),
                    etStatus.getText().toString()
            );

            Intent resultIntent = new Intent();
            resultIntent.putExtra("newVoucher", newVoucher);
            resultIntent.putExtra("position", editPosition);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
