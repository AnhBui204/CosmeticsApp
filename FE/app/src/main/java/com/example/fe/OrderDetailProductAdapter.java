package com.example.fe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.models.OrderItem; // SỬA

import java.util.List;
import java.util.Locale;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.ProductViewHolder> {

    private List<OrderItem> productList; // SỬA

    public OrderDetailProductAdapter(List<OrderItem> productList) { // SỬA
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OrderItem item = productList.get(position); // SỬA
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;
        // Thêm ImageView nếu bạn muốn hiển thị ảnh sản phẩm

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_product_quantity);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void bind(OrderItem item) { // SỬA
            tvProductName.setText(item.getName());
            tvQuantity.setText(String.format("x%d", item.getQuantity()));
            tvPrice.setText(String.format(Locale.US, "$%.2f", item.getPrice()));
            // TODO: Load item.getImage() vào ImageView
        }
    }
}