package com.example.fe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.models.Order;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;
    private final Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderNumber, tvDate, tvTrackingNumber, tvQuantity, tvSubtotal, tvStatus;
        MaterialButton btnDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTrackingNumber = itemView.findViewById(R.id.tv_tracking_number);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDetails = itemView.findViewById(R.id.btn_details);

            // === BẮT ĐẦU PHẦN THÊM MỚI ===
            // Thêm listener cho toàn bộ item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 1. Lấy vị trí item được click
                    int position = getAdapterPosition();

                    // 2. Đảm bảo vị trí hợp lệ
                    if (position != RecyclerView.NO_POSITION) {
                        Order order = orderList.get(position);

                        // 3. Tạo instance của OrderDetailFragment
                        // (Sử dụng phương thức newInstance chúng ta đã tạo)
                        Fragment detailFragment = OrderDetailFragment.newInstance(
                                order.getOrderNumber(),
                                order.getStatus()
                        );

                        // 4. Thực hiện chuyển Fragment
                        if (context instanceof FragmentActivity) {
                            FragmentActivity activity = (FragmentActivity) context;
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, detailFragment) // R.id.fragment_container là ID trong MainActivity
                                    .addToBackStack(null) // Thêm vào back stack để có thể quay lại
                                    .commit();
                        }
                    }
                }
            });
            // === KẾT THÚC PHẦN THÊM MỚI ===
        }

        public void bind(Order order) {
            tvOrderNumber.setText(String.format("Order #%s", order.getOrderNumber()));
            tvDate.setText(order.getDate());
            tvTrackingNumber.setText(String.format("Tracking number: %s", order.getTrackingNumber()));
            tvQuantity.setText(String.format("Quantity: %d", order.getQuantity()));
            tvSubtotal.setText(String.format(Locale.US, "Subtotal: $%.0f", order.getSubtotal()));
            tvStatus.setText(order.getStatus().toUpperCase());

            // Đặt màu cho trạng thái
            int statusColor;
            if ("Pending".equals(order.getStatus())) {
                statusColor = ContextCompat.getColor(context, R.color.orange_500); // Cần định nghĩa màu này
            } else if ("Delivered".equals(order.getStatus())) {
                statusColor = ContextCompat.getColor(context, R.color.green_500); // Cần định nghĩa màu này
            } else { // Cancelled
                statusColor = ContextCompat.getColor(context, R.color.red_500); // Cần định nghĩa màu này
            }
            tvStatus.setTextColor(statusColor);
        }
    }
}