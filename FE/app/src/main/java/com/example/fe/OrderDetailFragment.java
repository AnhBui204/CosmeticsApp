package com.example.fe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.OrderDetailProductAdapter;
import com.example.fe.api.ApiClient;
import com.example.fe.api.UserService;
import com.example.fe.models.Order;
import com.example.fe.models.OrderItem;
import com.example.fe.models.ProductItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_STATUS = "order_status";

    private String orderId;
    private String orderStatus;

    private RecyclerView recyclerViewProducts;
    private OrderDetailProductAdapter productAdapter;
    private final List<ProductItem> productList = new ArrayList<>();
    private TextView tvSubtotal, tvShipping, tvTotal;

    private TextView tvOrderNumber, tvAddress;
    private MaterialCardView bannerDelivered, bannerOnTheWay;
    private LinearLayout buttonGroupDelivered;
    private MaterialButton buttonContinueShopping;

    public static OrderDetailFragment newInstance(String orderCode, String status) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderCode);
        args.putString(ARG_ORDER_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
            orderStatus = getArguments().getString(ARG_ORDER_STATUS);
        }

        recyclerViewProducts = view.findViewById(R.id.recycler_view_products);
        tvOrderNumber = view.findViewById(R.id.tv_info_order_number);
        tvAddress = view.findViewById(R.id.tv_info_address);
        bannerDelivered = view.findViewById(R.id.banner_delivered);
        bannerOnTheWay = view.findViewById(R.id.banner_ontheway);
        buttonGroupDelivered = view.findViewById(R.id.button_group_delivered);
        buttonContinueShopping = view.findViewById(R.id.btn_continue_shopping);
        tvTotal = view.findViewById(R.id.tv_total_amount);

        setupRecyclerView();
        updateUiBasedOnStatus();
        loadOrderDataFromAPI();

        return view;
    }

    private void setupRecyclerView() {
        productAdapter = new OrderDetailProductAdapter(productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);
        recyclerViewProducts.setNestedScrollingEnabled(false);
    }

    private void updateUiBasedOnStatus() {
        if ("delivered".equalsIgnoreCase(orderStatus)) {
            bannerDelivered.setVisibility(View.VISIBLE);
            buttonGroupDelivered.setVisibility(View.VISIBLE);
            bannerOnTheWay.setVisibility(View.GONE);
            buttonContinueShopping.setVisibility(View.GONE);
        } else if ("cancelled".equalsIgnoreCase(orderStatus)) {
            bannerDelivered.setVisibility(View.GONE);
            buttonGroupDelivered.setVisibility(View.GONE);
            bannerOnTheWay.setVisibility(View.GONE);
            buttonContinueShopping.setVisibility(View.VISIBLE);
        } else {
            bannerOnTheWay.setVisibility(View.VISIBLE);
            buttonContinueShopping.setVisibility(View.VISIBLE);
            bannerDelivered.setVisibility(View.GONE);
            buttonGroupDelivered.setVisibility(View.GONE);
        }
    }

    private void loadOrderDataFromAPI() {
        UserService userService = ApiClient.getAuthClient(requireContext()).create(UserService.class);
        userService.getOrderDetail(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindOrderToViews(response.body());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindOrderToViews(Order order) {
        tvOrderNumber.setText("Order number: #" + order.getOrderCode());
        tvAddress.setText(order.getShippingAddress());

        productList.clear();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                productList.add(new ProductItem(item.getName(), item.getQuantity(), item.getPrice()));
            }
        }
        productAdapter.notifyDataSetChanged();

        tvTotal.setText(String.format(Locale.US, "$%.2f", order.getTotalAmount()));

        orderStatus = order.getStatus();
        updateUiBasedOnStatus();
    }
}
