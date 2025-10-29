package com.example.fe.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fe.R;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private final List<ProductModel> list;

    public RecommendedAdapter(List<ProductModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductModel item = list.get(position);
        holder.tvName.setText(item.name);
        holder.tvCategory.setText(item.category);
        holder.tvPrice.setText("Unit " + item.price);
        holder.imgProduct.setImageResource(item.image);
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
