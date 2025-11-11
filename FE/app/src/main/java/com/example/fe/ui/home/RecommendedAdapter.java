package com.example.fe.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe.R;
import com.example.fe.ui.category.Category;
import com.example.fe.ui.category.ProductDetailActivity; // ⬅️ nhớ import

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private final List<ProductModel> list;

    public RecommendedAdapter(List<ProductModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = list.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("Unit " + item.getPrice());

        // Load image: prefer imageUrl, fallback to imageRes
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(item.getImageRes());
        }

        Category category = item.getCategory();
        holder.tvCategory.setText(category != null ? category.getName() : "Unknown Category");

        // ➜ Click mở màn chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ProductDetailActivity.class);
            // only send productId to keep Intent small; ProductDetailActivity will fetch full details from API
            i.putExtra("productId", item.getId());
            i.putExtra("imageResId", item.getImageRes());
            try {
                v.getContext().startActivity(i);
            } catch (RuntimeException ex) {
                // TransactionTooLargeException or other runtime failure when sending too much data via Intent.
                android.util.Log.e("RecommendedAdapter", "Failed to start ProductDetailActivity with full extras, retrying with compact intent", ex);
                try {
                    Intent compact = new Intent(v.getContext(), ProductDetailActivity.class);
                    compact.putExtra("name", item.getName());
                    compact.putExtra("price", item.getPrice());
                    compact.putExtra("imageResId", item.getImageRes());
                    v.getContext().startActivity(compact);
                } catch (Exception ex2) {
                    android.util.Log.e("RecommendedAdapter", "Fallback startActivity also failed", ex2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvPrice;
        ImageView imgProduct;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
