package com.example.fe.ui.voucher;


import com.example.fe.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe.models.Voucher;
import com.example.fe.repository.VoucherRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddVoucherActivity extends AppCompatActivity {

    private EditText etCode, etTitle, etDescription, etEndDate, etStatus, etDiscountValue;
    private Spinner spinnerDiscountType;
    private Button btnSave;
    private Voucher editVoucher;
    private int editPosition = -1;
    private VoucherRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voucher);

        etCode = findViewById(R.id.etCode);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etEndDate = findViewById(R.id.etEndDate);
        etStatus = findViewById(R.id.etStatus);
        etDiscountValue = findViewById(R.id.etDiscountValue);
        spinnerDiscountType = findViewById(R.id.spinnerDiscountType);
        btnSave = findViewById(R.id.btnSaveVoucher);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        repository = new VoucherRepository(getApplicationContext());

        // setup spinner options: percentage or fixed_amount
        String[] types = new String[]{"percentage", "fixed_amount"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerDiscountType.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent.hasExtra("editVoucher")) {
            editVoucher = (Voucher) intent.getSerializableExtra("editVoucher");
            editPosition = intent.getIntExtra("position", -1);

            etCode.setText(editVoucher.getCode());
            etTitle.setText(editVoucher.getTitle());
            etDescription.setText(editVoucher.getDescription());
            etEndDate.setText(editVoucher.getEndDate());
            etStatus.setText(editVoucher.isActive() ? "ACTIVE" : "INACTIVE");
            if (editVoucher.getDiscountType() != null) {
                int pos = adapter.getPosition(editVoucher.getDiscountType());
                if (pos >= 0) spinnerDiscountType.setSelection(pos);
            }
            etDiscountValue.setText(String.valueOf(editVoucher.getDiscountValue()));
        }

        btnSave.setOnClickListener(v -> {
            if (etCode.getText() == null || etCode.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Mã voucher là bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            String chosenType = (String) spinnerDiscountType.getSelectedItem();
            String discountValueStr = etDiscountValue.getText() == null ? "" : etDiscountValue.getText().toString().trim();

            if (TextUtils.isEmpty(chosenType) || (!"percentage".equals(chosenType) && !"fixed_amount".equals(chosenType))) {
                Toast.makeText(this, "Chọn loại giảm giá: percentage hoặc fixed_amount", Toast.LENGTH_LONG).show();
                return;
            }

            double discountVal = 0;
            try {
                discountVal = Double.parseDouble(discountValueStr);
                if (discountVal <= 0) {
                    Toast.makeText(this, "Giá trị giảm giá phải lớn hơn 0", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Nhập giá trị giảm giá hợp lệ", Toast.LENGTH_LONG).show();
                return;
            }

            Voucher newVoucher = new Voucher();
            newVoucher.setCode(etCode.getText().toString().trim());
            newVoucher.setTitle(etTitle.getText().toString().trim());
            newVoucher.setDescription(etDescription.getText().toString().trim());
            newVoucher.setEndDate(etEndDate.getText().toString().trim());
            newVoucher.setActive("ACTIVE".equalsIgnoreCase(etStatus.getText().toString().trim()));

            // set discount fields required by BE
            newVoucher.setDiscountType(chosenType);
            newVoucher.setDiscountValue(discountVal);

            if (editVoucher != null) {
                // update
                repository.updateVoucher(editVoucher.getId(), newVoucher, new Callback<Voucher>() {
                    @Override
                    public void onResponse(Call<Voucher> call, Response<Voucher> response) {
                        if (response.isSuccessful()) {
                            Intent result = new Intent();
                            result.putExtra("newVoucher", response.body());
                            result.putExtra("position", editPosition);
                            setResult(RESULT_OK, result);
                            finish();
                        } else {
                            Toast.makeText(AddVoucherActivity.this, "Update failed: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Voucher> call, Throwable t) {
                        Toast.makeText(AddVoucherActivity.this, "Update error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // create
                repository.createVoucher(newVoucher, new Callback<Voucher>() {
                    @Override
                    public void onResponse(Call<Voucher> call, Response<Voucher> response) {
                        if (response.isSuccessful()) {
                            Intent result = new Intent();
                            result.putExtra("newVoucher", response.body());
                            setResult(RESULT_OK, result);
                            finish();
                        } else {
                            Toast.makeText(AddVoucherActivity.this, "Create failed: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Voucher> call, Throwable t) {
                        Toast.makeText(AddVoucherActivity.this, "Create error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
