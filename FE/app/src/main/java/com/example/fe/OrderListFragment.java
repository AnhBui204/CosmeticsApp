package com.example.fe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fe.models.Order;
import com.example.fe.models.OrderViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private static final String ARG_STATUS = "order_status";
    private String orderStatus;

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    private OrderViewModel viewModel;
    private ProgressBar progressBar;
    private TextView tvEmptyList;

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderStatus = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_orders);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyList = view.findViewById(R.id.tv_empty_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, getContext(), this); // ✅ truyền listener
        recyclerView.setAdapter(orderAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                tvEmptyList.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getMyOrdersLiveData().observe(getViewLifecycleOwner(), allOrders -> {
            if (allOrders != null) {
                List<Order> filteredList = allOrders.stream()
                        .filter(order -> filterByStatus(order.getStatus()))
                        .collect(Collectors.toList());

                orderList.clear();
                orderList.addAll(filteredList);
                orderAdapter.notifyDataSetChanged();

                if (filteredList.isEmpty()) {
                    tvEmptyList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyList.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean filterByStatus(String apiStatus) {
        if (apiStatus == null) return false;
        if ("pending".equalsIgnoreCase(orderStatus)) {
            return "pending".equalsIgnoreCase(apiStatus) || "processing".equalsIgnoreCase(apiStatus);
        }
        return orderStatus.equalsIgnoreCase(apiStatus);
    }

    // ✅ Xử lý khi click
    @Override
    public void onOrderClick(Order order) {
        if (!isAdded()) return;

        Fragment detailFragment = OrderDetailFragment.newInstance(
                order.getOrderNumber(),
                order.getStatus()
        );

        // ⚙️ Dùng container ID thật (thường là fragment_container trong Activity chính)
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
