package com.example.fe.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe.R;
import com.example.fe.ui.home.ProductModel;

import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.VH> {

    private final List<ProductModel> items;

    public CheckoutAdapter(List<ProductModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ProductModel p = items.get(position);
        holder.tvName.setText(p.getName());
        holder.tvQty.setText(String.format(Locale.US, "Qty: %d", p.getQuantity()));
        holder.tvPrice.setText(p.getPrice());
        String url = p.getImageUrl();
        if (url != null && !url.isEmpty()) Glide.with(holder.itemView.getContext()).load(url).into(holder.img);
        else holder.img.setImageResource(p.getImageRes());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvQty, tvPrice;
        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
