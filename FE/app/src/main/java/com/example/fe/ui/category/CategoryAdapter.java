package com.example.fe.ui.category;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;

    public CategoryAdapter(@NonNull List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        // Bind UI
        holder.tvName.setText(c.getName());
        holder.tvDesc.setText(c.getDescription());
        holder.tvCategoryID.setText(c.getCategoryID()); // đang tái dùng id text price để show mã
        holder.imgCategory.setImageResource(c.getImageResId());
        holder.imgCategory.setContentDescription(c.getName());

        // Click item
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Category selected = categoryList.get(pos);

            Intent i = new Intent(v.getContext(), ProductListActivity.class);
            // ✅ Chỉ truyền ID (hạn chế trùng dữ liệu). ProductListActivity sẽ lấy tên từ AppData.
            i.putExtra("categoryId", selected.getCategoryID());
            v.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvName, tvDesc, tvCategoryID;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // mapping đúng theo layout item_category hiện tại của bạn
            imgCategory  = itemView.findViewById(R.id.imgProduct);
            tvName       = itemView.findViewById(R.id.tvProductName);
            tvDesc       = itemView.findViewById(R.id.tvProductDesc);
            tvCategoryID = itemView.findViewById(R.id.tvProductPrice); // tái dùng id cũ
        }
    }
}
