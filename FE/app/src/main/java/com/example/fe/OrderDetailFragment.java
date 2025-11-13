package com.example.fe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.models.Order;
import com.example.fe.models.OrderItem;
import com.example.fe.models.OrderViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_ORDER_CODE = "order_code"; // Sửa tên
    private static final String ARG_ORDER_STATUS = "order_status";

    private String orderCode;
    private String orderStatus;

    // Views
    private Toolbar toolbar;
    private TextView tvToolbarTitle, tvOrderNumber, tvTrackingNumber, tvAddress;
    private MaterialCardView bannerDelivered, bannerOnTheWay;
    private LinearLayout buttonGroupDelivered;
    private MaterialButton buttonContinueShopping;
    private RecyclerView recyclerViewProducts;
    private ProgressBar progressBar;
    private View contentContainer; // Layout chứa nội dung

    private OrderDetailProductAdapter productAdapter;
    private List<OrderItem> productList; // Sửa

    private OrderViewModel viewModel; // Thêm

    public static OrderDetailFragment newInstance(String orderCode, String status) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_CODE, orderCode);
        args.putString(ARG_ORDER_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderCode = getArguments().getString(ARG_ORDER_CODE);
            orderStatus = getArguments().getString(ARG_ORDER_STATUS); // Dùng để hiển thị tạm thời
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        bindViews(view);
        setupToolbar();
        setupRecyclerView();
        updateUiBasedOnStatus(orderStatus); // Hiển thị tạm thời
        setupClickListeners(view);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        // Tải dữ liệu từ API
        loadOrderDataFromAPI();
        observeViewModel();

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
        progressBar = view.findViewById(R.id.progress_bar); // Cần thêm vào XML
        contentContainer = view.findViewById(R.id.content_container); // Cần thêm vào XML

        tvOrderNumber = view.findViewById(R.id.tv_info_order_number);
        tvTrackingNumber = view.findViewById(R.id.tv_info_tracking_number); // Sẽ ẩn đi
        tvAddress = view.findViewById(R.id.tv_info_address);

        // Ẩn tvTrackingNumber vì không có trong API
        if(tvTrackingNumber != null) {
            tvTrackingNumber.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        tvToolbarTitle.setText("Order #" + orderCode);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new OrderDetailProductAdapter(productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);
        recyclerViewProducts.setNestedScrollingEnabled(false);
    }

    private void updateUiBasedOnStatus(String status) {
        if ("delivered".equalsIgnoreCase(status)) {
            bannerDelivered.setVisibility(View.VISIBLE);
            buttonGroupDelivered.setVisibility(View.VISIBLE);
            bannerOnTheWay.setVisibility(View.GONE);
            buttonContinueShopping.setVisibility(View.GONE);
        } else { // "pending", "processing", "shipped", "cancelled"
            bannerOnTheWay.setVisibility(View.VISIBLE);
            buttonContinueShopping.setVisibility(View.VISIBLE);
            bannerDelivered.setVisibility(View.GONE);
            buttonGroupDelivered.setVisibility(View.GONE);

            // Nếu là "cancelled" thì có thể thay đổi text banner
            if("cancelled".equalsIgnoreCase(status)) {
                // (Bạn có thể đổi text của bannerOnTheWay tại đây)
            }
        }
    }

    private void setupClickListeners(View view) {
        // ... (Giữ nguyên các click listener của bạn)
    }

    private void loadOrderDataFromAPI() {
        viewModel.fetchOrderDetails(orderCode);
    }

    private void observeViewModel() {
        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            contentContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.getOrderDetailsLiveData().observe(getViewLifecycleOwner(), order -> {
            if (order != null && order.getOrderNumber().equals(orderCode)) {
                // Cập nhật toàn bộ UI với dữ liệu mới
                tvToolbarTitle.setText("Order #" + order.getOrderNumber());
                tvOrderNumber.setText("Order number: #" + order.getOrderNumber());

                if (order.getShippingAddress() != null) {
                    tvAddress.setText("Delivery address: " + order.getShippingAddress().toString());
                }

                // Cập nhật danh sách sản phẩm
                productList.clear();
                if (order.getItems() != null) {
                    productList.addAll(order.getItems());
                }
                productAdapter.notifyDataSetChanged();

                // Cập nhật banner dựa trên status thật
                updateUiBasedOnStatus(order.getStatus());

                // TODO: Cập nhật các trường tiền (Subtotal, Shipping, Total)
                // Bạn cần thêm TextViews cho chúng trong XML
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if(error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}