package com.example.fe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.OrderDetailProductAdapter;
import com.example.fe.models.ProductItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_STATUS = "order_status";

    private String orderId;
    private String orderStatus;

    // Views
    private Toolbar toolbar;
    private TextView tvToolbarTitle, tvOrderNumber, tvTrackingNumber, tvAddress;
    private MaterialCardView bannerDelivered, bannerOnTheWay;
    private LinearLayout buttonGroupDelivered;
    private MaterialButton buttonContinueShopping;
    private RecyclerView recyclerViewProducts;

    private OrderDetailProductAdapter productAdapter;
    private List<ProductItem> productList;

    public static OrderDetailFragment newInstance(String orderId, String status) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_ORDER_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
            orderStatus = getArguments().getString(ARG_ORDER_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        bindViews(view);
        setupToolbar();
        setupRecyclerView();
        updateUiBasedOnStatus();
        setupClickListeners(view);
        loadOrderData(); // Tải dữ liệu giả

        return view;
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tvToolbarTitle = view.findViewById(R.id.toolbar_title);
        bannerDelivered = view.findViewById(R.id.banner_delivered);
        bannerOnTheWay = view.findViewById(R.id.banner_ontheway);
        buttonGroupDelivered = view.findViewById(R.id.button_group_delivered);
        buttonContinueShopping = view.findViewById(R.id.btn_continue_shopping);
        recyclerViewProducts = view.findViewById(R.id.recycler_view_products);

        // Views trong card thông tin
        tvOrderNumber = view.findViewById(R.id.tv_info_order_number);
        tvTrackingNumber = view.findViewById(R.id.tv_info_tracking_number);
        tvAddress = view.findViewById(R.id.tv_info_address);
    }

    private void setupToolbar() {
        tvToolbarTitle.setText("Order #" + orderId);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new OrderDetailProductAdapter(productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);
        recyclerViewProducts.setNestedScrollingEnabled(false); // Quan trọng khi dùng trong NestedScrollView
    }

    private void updateUiBasedOnStatus() {
        if ("Delivered".equals(orderStatus)) {
            bannerDelivered.setVisibility(View.VISIBLE);
            buttonGroupDelivered.setVisibility(View.VISIBLE);
            bannerOnTheWay.setVisibility(View.GONE);
            buttonContinueShopping.setVisibility(View.GONE);
        } else { // "Pending" or "On the way"
            bannerOnTheWay.setVisibility(View.VISIBLE);
            buttonContinueShopping.setVisibility(View.VISIBLE);
            bannerDelivered.setVisibility(View.GONE);
            buttonGroupDelivered.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners(View view) {
        // Nút Rate
        view.findViewById(R.id.btn_rate).setOnClickListener(v -> {
            // Chuyển qua RateProductFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RateProductFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Banner "Track your order"
        bannerOnTheWay.setOnClickListener(v -> {
            // Chuyển qua TrackOrderFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TrackOrderFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // TODO: Thêm listener cho "Return Home" và "Continue Shopping"
    }

    // Tải dữ liệu giả
    private void loadOrderData() {
        tvOrderNumber.setText("Order number: #" + orderId);

        productList.clear();
        if ("Delivered".equals(orderStatus)) {
            tvTrackingNumber.setText("Tracking Number: IK987362341");
            tvAddress.setText("Delivery address: SBI Building, Software Park");
            productList.add(new ProductItem("Maxi Dress", 1, 68.00));
            productList.add(new ProductItem("Linen Dress", 1, 52.00));
        } else {
            tvTrackingNumber.setText("Tracking Number: IK287368838");
            tvAddress.setText("Delivery address: SBI Building, Software Park");
            productList.add(new ProductItem("Sportwear Set", 1, 80.00));
            productList.add(new ProductItem("Cotton T-shirt", 1, 30.00));
        }
        productAdapter.notifyDataSetChanged();

        // TODO: Cập nhật Subtotal, Shipping, Total...
    }
}
