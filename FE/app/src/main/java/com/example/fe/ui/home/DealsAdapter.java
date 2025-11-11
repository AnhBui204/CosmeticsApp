package com.example.fe.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fe.R;
import java.util.List;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealViewHolder> {
    private List<DealModel> dealList;

    public DealsAdapter(List<DealModel> dealList) {
        this.dealList = dealList;
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deal, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        DealModel deal = dealList.get(position);
        holder.tvPrice.setText(deal.getPrice());
        holder.tvDescription.setText(deal.getDescription());
        holder.imgProduct.setImageResource(deal.getImageRes());
    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }

    static class DealViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvPrice;
        TextView tvDescription;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgDealProduct);
            tvPrice = itemView.findViewById(R.id.tvDealPrice);
            tvDescription = itemView.findViewById(R.id.tvDealDescription);
        }
    }
}