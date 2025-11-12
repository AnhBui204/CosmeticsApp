package com.example.fe.ui.voucher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.fe.R;
import com.example.fe.models.VoucherItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<VoucherItem> allVouchers;
    private List<VoucherItem> filteredVouchers;

    private TextView chipAll, chipActive, chipUpcoming, chipExpired;
    private FloatingActionButton fabAdd;

    private static final int REQ_ADD_VOUCHER = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.seller_fragment_manage_voucher, container, false);

        recyclerView = view.findViewById(R.id.recyclerVouchers);
        fabAdd = view.findViewById(R.id.fabAddVoucher);
        chipAll = view.findViewById(R.id.chipAll);
        chipActive = view.findViewById(R.id.chipActive);
        chipUpcoming = view.findViewById(R.id.chipUpcoming);
        chipExpired = view.findViewById(R.id.chipExpired);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- Dữ liệu mẫu ---
        allVouchers = new ArrayList<>();
        allVouchers.add(new VoucherItem("VC1001", "10% OFF", "Min $30", "2025-12-01", "ACTIVE"));
        allVouchers.add(new VoucherItem("VC1002", "Free Shipping", "Min $20", "2025-11-20", "UPCOMING"));
        allVouchers.add(new VoucherItem("VC1003", "$5 OFF", "Min $50", "2024-09-10", "EXPIRED"));
        allVouchers.add(new VoucherItem("VC1004", "20% OFF", "Min $40", "2025-12-10", "ACTIVE"));

        filteredVouchers = new ArrayList<>(allVouchers);
        adapter = new VoucherAdapter(getContext(), filteredVouchers);
        recyclerView.setAdapter(adapter);

        // --- Thiết lập chip ---
        setupChipListeners();
        highlightChip(chipAll); // mặc định All được chọn
        filterVouchers("ALL");

        // --- Nút thêm voucher ---
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddVoucherActivity.class);
            startActivityForResult(intent, REQ_ADD_VOUCHER);
        });

        return view;
    }

    // ----------------- CHIP LOGIC -----------------
    private void setupChipListeners() {
        chipAll.setOnClickListener(v -> {
            highlightChip(chipAll);
            filterVouchers("ALL");
        });

        chipActive.setOnClickListener(v -> {
            highlightChip(chipActive);
            filterVouchers("ACTIVE");
        });

        chipUpcoming.setOnClickListener(v -> {
            highlightChip(chipUpcoming);
            filterVouchers("UPCOMING");
        });

        chipExpired.setOnClickListener(v -> {
            highlightChip(chipExpired);
            filterVouchers("EXPIRED");
        });
    }


    private void highlightChip(TextView selectedChip) {
        TextView[] chips = {chipAll, chipActive, chipUpcoming, chipExpired};

        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_outline);
            chip.setTextColor(getResources().getColor(R.color.gray_700));
        }


        selectedChip.setBackgroundResource(R.drawable.bg_chip_filled);
        selectedChip.setTextColor(Color.WHITE);
    }

    /**
     * Lọc danh sách voucher theo trạng thái (All / Active / Upcoming / Expired)
     */
    private void filterVouchers(String status) {
        filteredVouchers.clear();

        if (status.equals("ALL")) {
            filteredVouchers.addAll(allVouchers);
        } else {
            for (VoucherItem v : allVouchers) {
                if (v.getStatus().equalsIgnoreCase(status)) {
                    filteredVouchers.add(v);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ----------------- CRUD -----------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD_VOUCHER && resultCode == Activity.RESULT_OK && data != null) {
            VoucherItem newVoucher = (VoucherItem) data.getSerializableExtra("newVoucher");
            int pos = data.getIntExtra("position", -1);

            if (pos >= 0) { // Update voucher
                allVouchers.set(pos, newVoucher);
            } else { // Add new voucher
                allVouchers.add(newVoucher);
            }

            filterVouchers("ALL");
            highlightChip(chipAll);
        }
    }
}
