package com.example.fe.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.imgProduct.setImageResource(item.getImage());

        Category category = item.getCategory();
        holder.tvCategory.setText(category != null ? category.getName() : "Unknown Category");

        // ➜ Click mở màn chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ProductDetailActivity.class);
            i.putExtra("name", item.getName());
            i.putExtra("price", item.getPrice());
            i.putExtra("imageResId", item.getImage());
            i.putExtra("categoryName", category != null ? category.getName() : "");
            v.getContext().startActivity(i);
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
