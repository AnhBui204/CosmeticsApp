package com.example.fe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.models.Order;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;
    private final Context context;
    private final OnOrderClickListener listener; // ✅ thêm listener

    // ✅ Constructor có listener
    public OrderAdapter(List<Order> orderList, Context context, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderNumber, tvDate, tvQuantity, tvSubtotal, tvStatus;
        MaterialButton btnDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDetails = itemView.findViewById(R.id.btn_details);

            // ✅ Click gọi listener (an toàn)
            View.OnClickListener clickListener = v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOrderClick(orderList.get(position));
                }
            };

            itemView.setOnClickListener(clickListener);
            btnDetails.setOnClickListener(clickListener);
        }

        public void bind(Order order) {
            tvOrderNumber.setText(String.format("Order #%s", order.getOrderNumber()));

            if (order.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                sdf.setTimeZone(TimeZone.getDefault());
                tvDate.setText(sdf.format(order.getDate()));
            } else {
                tvDate.setText("N/A");
            }

            tvQuantity.setText(String.format("Quantity: %d", order.getQuantity()));
            tvSubtotal.setText(String.format(Locale.US, "Subtotal: $%.0f", order.getTotalAmount()));

            String status = order.getStatus() != null ? order.getStatus().toUpperCase() : "UNKNOWN";
            tvStatus.setText(status);

            int statusColor;
            if ("pending".equalsIgnoreCase(order.getStatus()) || "processing".equalsIgnoreCase(order.getStatus())) {
                statusColor = ContextCompat.getColor(context, R.color.orange_500);
            } else if ("delivered".equalsIgnoreCase(order.getStatus())) {
                statusColor = ContextCompat.getColor(context, R.color.green_500);
            } else if ("cancelled".equalsIgnoreCase(order.getStatus())) {
                statusColor = ContextCompat.getColor(context, R.color.red_500);
            } else {
                statusColor = ContextCompat.getColor(context, R.color.blue_500);
            }
            tvStatus.setTextColor(statusColor);
        }
    }

    // ✅ Interface click
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}
