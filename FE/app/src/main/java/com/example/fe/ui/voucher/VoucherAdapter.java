package com.example.fe.ui.voucher;
import com.example.fe.R;
import com.example.fe.models.Voucher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private Context context;
    private List<Voucher> voucherList;
    private OnDeleteCallback onDelete;
    private OnEditCallback onEdit;

    public interface OnDeleteCallback { void onDelete(String id); }
    public interface OnEditCallback { void onEdit(Voucher voucher, int position); }

    public VoucherAdapter(Context context, List<Voucher> voucherList, OnDeleteCallback onDelete, OnEditCallback onEdit) {
        this.context = context;
        this.voucherList = voucherList;
        this.onDelete = onDelete;
        this.onEdit = onEdit;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        holder.tvCode.setText(voucher.getCode());
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvDescription.setText(voucher.getDescription());
        holder.tvEndDate.setText(voucher.getEndDate());
        holder.tvStatus.setText(voucher.isActive() ? "ACTIVE" : "INACTIVE");

        holder.btnEdit.setOnClickListener(v -> {
            if (onEdit != null) onEdit.onEdit(voucher, position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete voucher")
                    .setMessage("Are you sure you want to delete " + voucher.getCode() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (onDelete != null) onDelete.onDelete(voucher.getId());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvTitle, tvDescription, tvEndDate, tvStatus;
        Button btnEdit, btnDelete;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
