package com.example.fe.ui.seller.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.DatePickerDialog;
import android.widget.Toast;
import java.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fe.R;
import com.example.fe.models.Order;
import com.example.fe.models.OrderItem;
import com.example.fe.models.ShippingAddress;
import com.example.fe.ui.seller.SellerOrderDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SellerOrdersFragment extends Fragment {

    private TextView tvPending, tvDelivered, tvCancelled;

    public SellerOrdersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seller_orders, container, false);

        tvPending = view.findViewById(R.id.tvPending);
        tvDelivered = view.findViewById(R.id.tvDelivered);
        tvCancelled = view.findViewById(R.id.tvCancelled);

        // Hiển thị mặc định tab Delivered
        showFragment(OrdersListFragment.newInstance("DELIVERED"));
        setSelectedTab(tvDelivered);

        tvPending.setOnClickListener(v -> {
            showFragment(OrdersListFragment.newInstance("PENDING"));
            setSelectedTab(tvPending);
        });

        tvDelivered.setOnClickListener(v -> {
            showFragment(OrdersListFragment.newInstance("DELIVERED"));
            setSelectedTab(tvDelivered);
        });

        tvCancelled.setOnClickListener(v -> {
            showFragment(OrdersListFragment.newInstance("CANCELLED"));
            setSelectedTab(tvCancelled);
        });

        // Kết nối thanh search và filter ở activity (nếu có)
        EditText etOrderSearch = requireActivity().findViewById(R.id.etOrderSearch);
        ImageButton btnFilterDate = requireActivity().findViewById(R.id.btnFilterDate);

        if (etOrderSearch != null) {
            etOrderSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Fragment current = getChildFragmentManager().findFragmentById(R.id.ordersFragmentContainer);
                    if (current instanceof OrdersListFragment) {
                        ((OrdersListFragment) current).filter(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnFilterDate != null) {
            btnFilterDate.setOnClickListener(v -> {
                // open date picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(requireContext(), (view1, y, m, d) -> {
                    // format yyyy-MM-dd
                    String mm = String.format(Locale.US, "%02d", m + 1);
                    String dd = String.format(Locale.US, "%02d", d);
                    String picked = y + "-" + mm + "-" + dd;
                    // apply date filter to current tab fragment
                    Fragment current = getChildFragmentManager().findFragmentById(R.id.ordersFragmentContainer);
                    if (current instanceof OrdersListFragment) {
                        ((OrdersListFragment) current).filterByDate(picked);
                        Toast.makeText(requireContext(), "Filtered by: " + picked, Toast.LENGTH_SHORT).show();
                    }
                }, year, month, day);
                dpd.show();
            });
        }

        // TODO: Xử lý nút filter ngày nếu cần
        return view;
    }

    private void showFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.ordersFragmentContainer, fragment)
                .commit();
    }

    private void setSelectedTab(TextView selected) {
        tvPending.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvDelivered.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvCancelled.setBackgroundResource(R.drawable.bg_tab_unselected);

        tvPending.setTextColor(Color.BLACK);
        tvDelivered.setTextColor(Color.BLACK);
        tvCancelled.setTextColor(Color.BLACK);

        selected.setBackgroundResource(R.drawable.bg_tab_selected);
        selected.setTextColor(Color.WHITE);
    }

    // Fragment hiển thị danh sách đơn hàng theo trạng thái
    public static class OrdersListFragment extends Fragment {

        private static final String ARG_STATUS = "arg_status";
        private String status;
        private OrdersAdapter adapter;

        public static OrdersListFragment newInstance(String status) {
            OrdersListFragment fragment = new OrdersListFragment();
            Bundle args = new Bundle();
            args.putString(ARG_STATUS, status);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                status = getArguments().getString(ARG_STATUS);
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
            RecyclerView recyclerView = view.findViewById(R.id.recyclerOrdersList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Create sample Order data
            List<Order> data = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                Order order = new Order();
                order.setOrderCode("ORD" + (1000 + i));
                order.setStatus(status.equals("ALL")
                        ? (i % 3 == 0 ? "DELIVERED" : (i % 2 == 0 ? "PENDING" : "CANCELLED"))
                        : status);
                order.setCreatedAt("2025-11-" + String.format(Locale.US, "%02d", i));

                // ShippingAddress as String
                order.setShippingAddress("Customer " + i + " address");

                // Items fake
                List<OrderItem> items = new ArrayList<>();
                for (int j = 1; j <= 2; j++) {
                    OrderItem item = new OrderItem();
                    item.setName((j % 2 == 0) ? "Lipstick" : "Serum");
                    item.setQuantity(j);
                    item.setPrice(20.0 + j);
                    items.add(item);
                }
                order.setItems(items);

                // Tổng tiền
                double total = 0;
                for (OrderItem it : items) {
                    total += it.getPrice() * it.getQuantity();
                }
                order.setTotalAmount(total);

                data.add(order);
            }

            adapter = new OrdersAdapter(data);
            recyclerView.setAdapter(adapter);

            adapter = new OrdersAdapter(data);
            recyclerView.setAdapter(adapter);
            return view;
        }

        public void filter(String q) {
            if (adapter != null) adapter.setTextFilter(q);
        }

        public void filterByDate(String date) {
            if (adapter != null) adapter.setDateFilter(date);
        }
    }

    // Adapter using Order model
    static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {
        private final List<Order> original;
        private final List<Order> displayed;

        OrdersAdapter(List<Order> data) {
            this.original = new ArrayList<>(data);
            this.displayed = new ArrayList<>(data);
        }

        private String textFilter = null;
        private String dateFilter = null; // yyyy-MM-dd

        public void setTextFilter(String q) {
            this.textFilter = (q == null || q.trim().isEmpty()) ? null : q.trim();
            applyFilters();
        }

        public void setDateFilter(String date) {
            this.dateFilter = (date == null || date.trim().isEmpty()) ? null : date.trim();
            applyFilters();
        }

        private void applyFilters() {
            displayed.clear();
            for (Order o : original) {
                boolean matchText = true;
                if (textFilter != null) {
                    String lower = textFilter.toLowerCase();

                    // Order code
                    boolean byCode = o.getOrderCode() != null && o.getOrderCode().toLowerCase().contains(lower);

                    // Customer name from shippingAddress
                    boolean byCustomer = o.getShippingAddress() != null &&
                            o.getShippingAddress() != null &&
                            o.getShippingAddress().toLowerCase().contains(lower);

                    // Items summary: nối tên tất cả item
                    boolean byItems = false;
                    if (o.getItems() != null) {
                        StringBuilder itemsSummary = new StringBuilder();
                        for (OrderItem it : o.getItems()) {
                            itemsSummary.append(it.getName()).append(", ");
                        }
                        byItems = itemsSummary.toString().toLowerCase().contains(lower);
                    }

                    matchText = byCode || byCustomer || byItems;
                }

                boolean matchDate = true;
                if (dateFilter != null) {
                    matchDate = dateFilter.equals(o.getCreatedAt());
                }
                if (matchText && matchDate) displayed.add(o);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_order, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Order o = displayed.get(position);

            // Order code
            holder.tvOrderId.setText(o.getOrderCode());

            // Created date
            holder.tvOrderDate.setText(o.getCreatedAt());

            // Quantity tổng số sản phẩm
            int totalQuantity = 0;
            if (o.getItems() != null) {
                for (OrderItem it : o.getItems()) totalQuantity += it.getQuantity();
            }
            final int finalQuantity = totalQuantity;
            holder.tvQuantity.setText("Quantity: " + finalQuantity);

            // Subtotal
            holder.tvSubtotal.setText(String.format(Locale.US, "Subtotal: $%.2f", o.getTotalAmount()));

            // Customer name
            // Customer name
            holder.tvCustomerName.setText(o.getShippingAddress() != null ? o.getShippingAddress() : "N/A");

            // Items summary
            StringBuilder itemsSummary = new StringBuilder();
            if (o.getItems() != null) {
                for (OrderItem it : o.getItems()) {
                    itemsSummary.append(it.getName()).append(", ");
                }
                if (itemsSummary.length() > 2) itemsSummary.setLength(itemsSummary.length() - 2); // remove last ", "
            }
            holder.tvOrderItems.setText(itemsSummary.toString());

            // Status
            holder.tvOrderStatus.setText(o.getStatus());
            if (o.getStatus() != null) {
                String st = o.getStatus().trim().toUpperCase();
                int color;
                switch (st) {
                    case "PENDING": color = Color.parseColor("#CF6212"); break;
                    case "DELIVERED": color = Color.parseColor("#009254"); break;
                    case "CANCELLED": color = Color.parseColor("#C50000"); break;
                    default: color = Color.parseColor("#7A7A7A");
                }
                holder.tvOrderStatus.setTextColor(color);
            }

            holder.btnDetails.setOnClickListener(v -> {
                android.content.Context ctx = v.getContext();
                android.content.Intent intent = new android.content.Intent(ctx, SellerOrderDetailActivity.class);
                intent.putExtra("order_id", o.getOrderCode());
                intent.putExtra("order_date", o.getCreatedAt());
                intent.putExtra("customer", o.getShippingAddress() != null ? o.getShippingAddress() : "N/A");

                intent.putExtra("status", o.getStatus());
                intent.putExtra("subtotal", o.getTotalAmount());
                intent.putExtra("quantity", finalQuantity);
                ctx.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return displayed.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvOrderId, tvOrderDate, tvTrackingNumber, tvQuantity, tvSubtotal, tvCustomerName, tvOrderItems, tvOrderStatus;
            com.google.android.material.button.MaterialButton btnDetails;

            VH(@NonNull View v) {
                super(v);
                tvOrderId = v.findViewById(R.id.tv_order_number);
                tvOrderDate = v.findViewById(R.id.tv_date);
                tvTrackingNumber = v.findViewById(R.id.tv_tracking_number);
                tvQuantity = v.findViewById(R.id.tv_quantity);
                tvSubtotal = v.findViewById(R.id.tv_subtotal);
                tvCustomerName = v.findViewById(R.id.tv_customer_name);
                tvOrderItems = v.findViewById(R.id.tv_items_summary);
                tvOrderStatus = v.findViewById(R.id.tv_status);
                btnDetails = v.findViewById(R.id.btn_details);
            }
        }
    }
}
