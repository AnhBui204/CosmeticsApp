package com.example.fe.ui.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fe.R;
import com.example.fe.data.TopProductData;
import com.bumptech.glide.Glide;
import android.util.Log;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.ProductViewHolder> {

    private List<TopProductData> productList;
    private NumberFormat currencyFormatter; // <-- Thêm formatter

    public TopProductAdapter(List<TopProductData> productList) {
        this.productList = productList;
        // Khởi tạo formatter (ví dụ: 120000 -> 120.000)
        this.currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_product, parent, false);
        return new ProductViewHolder(view);
    }

    

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductName, tvProductSold;
        TextView tvProductRevenue;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.imageView_product);
            tvProductName = itemView.findViewById(R.id.textView_productName);
            tvProductSold = itemView.findViewById(R.id.textView_soldCount);
            tvProductRevenue = itemView.findViewById(R.id.textView_productRevenue); // <-- 2. Ánh xạ
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        TopProductData product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductSold.setText("Đã bán: " + product.getTotalQuantity());

        String revenueString = currencyFormatter.format(product.getTotalRevenue()) + "đ";
        holder.tvProductRevenue.setText(revenueString);

        // Load image if available, otherwise show a placeholder
        String img = product.getImage();
    Log.d("TopProductAdapter", "bind productId=" + product.getProductId() + ", image=" + img);
        if (img != null && !img.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(img)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(R.drawable.ic_image_placeholder);
        }
    }
}