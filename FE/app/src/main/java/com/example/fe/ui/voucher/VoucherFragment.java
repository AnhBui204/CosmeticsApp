package com.example.fe.ui.voucher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.models.Voucher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<Voucher> allVouchers;

    private TextView chipAll, chipActive, chipUpcoming, chipExpired;
    private FloatingActionButton fabAdd;

    private ActivityResultLauncher<Intent> voucherLauncher;
    private VoucherViewModel viewModel;

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

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(VoucherViewModel.class);

        allVouchers = new ArrayList<>();
        adapter = new VoucherAdapter(getContext(), allVouchers, new VoucherAdapter.OnDeleteCallback() {
            @Override
            public void onDelete(String id) {
                deleteVoucher(id);
            }
        }, new VoucherAdapter.OnEditCallback() {
            @Override
            public void onEdit(Voucher voucher, int position) {
                Intent intent = new Intent(getContext(), AddVoucherActivity.class);
                intent.putExtra("editVoucher", voucher);
                intent.putExtra("position", position);
                // use ActivityResultLauncher to open AddVoucherActivity
                if (voucherLauncher != null) voucherLauncher.launch(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // register launcher for add/edit voucher
        voucherLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                Voucher returned = (Voucher) data.getSerializableExtra("newVoucher");
                int pos = data.getIntExtra("position", -1);
                if (returned == null) return; // nothing to do

                // If activity already performed create/update on server and returned the created/updated object,
                // update UI immediately instead of reloading from server.
                if (pos >= 0) {
                    // update existing item in list
                    if (pos < allVouchers.size()) {
                        allVouchers.set(pos, returned);
                        adapter.notifyItemChanged(pos);
                    } else {
                        // fallback: append if index out of range
                        allVouchers.add(returned);
                        adapter.notifyItemInserted(allVouchers.size() - 1);
                    }
                } else {
                    // new voucher: add to top
                    allVouchers.add(0, returned);
                    adapter.notifyItemInserted(0);
                    // scroll to top
                    RecyclerView rv = recyclerView;
                    if (rv != null) rv.scrollToPosition(0);
                }
            }
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddVoucherActivity.class);
            voucherLauncher.launch(intent);
        });

        // observe
        viewModel.vouchers.observe(getViewLifecycleOwner(), vouchers -> {
            allVouchers.clear();
            if (vouchers != null) allVouchers.addAll(vouchers);
            adapter.notifyDataSetChanged();
        });

        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null) Log.e("VoucherFragment", "Error: " + err);
        });

        viewModel.loadVouchers();

        return view;
    }

    private void deleteVoucher(String id) {
        viewModel.deleteVoucher(id, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    viewModel.loadVouchers();
                } else {
                    Log.e("VoucherFragment", "Delete failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("VoucherFragment", "Delete error: " + t.getMessage());
            }
        });
    }

    // ----------------- CHIP LOGIC (kept for UI filter) -----------------
    private void setupChipListeners() {
        chipAll.setOnClickListener(v -> highlightChip(chipAll));
        chipActive.setOnClickListener(v -> highlightChip(chipActive));
        chipUpcoming.setOnClickListener(v -> highlightChip(chipUpcoming));
        chipExpired.setOnClickListener(v -> highlightChip(chipExpired));
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
}
