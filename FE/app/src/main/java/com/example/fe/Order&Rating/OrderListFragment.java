package com.example.fe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "order_status";
    private String orderStatus;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    // Phương thức factory để tạo instance mới của fragment với tham số
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, getContext());
        recyclerView.setAdapter(orderAdapter);

        // Tải dữ liệu (ở đây dùng dữ liệu giả)
        loadDummyData(orderStatus);

        return view;
    }

    // Phương thức tải dữ liệu giả dựa trên status
    private void loadDummyData(String status) {
        orderList.clear();
        if ("Pending".equals(status)) {
            orderList.add(new Order("1524", "13/05/2021", "IK287368838", 2, 110.0, "Pending"));
            orderList.add(new Order("1524", "12/05/2021", "IK2873218897", 3, 230.0, "Pending"));
            orderList.add(new Order("1524", "10/05/2021", "IK237368820", 5, 490.0, "Pending"));
        } else if ("Delivered".equals(status)) {
            orderList.add(new Order("1514", "13/05/2021", "IK987362341", 2, 110.0, "Delivered"));
            orderList.add(new Order("1679", "12/05/2021", "IK3873218890", 3, 450.0, "Delivered"));
            orderList.add(new Order("1671", "10/05/2021", "IK237368881", 3, 400.0, "Delivered"));
        } else if ("Cancelled".equals(status)) {
            orderList.add(new Order("1829", "10/05/2021", "IK287368831", 2, 210.0, "Cancelled"));
            orderList.add(new Order("1824", "10/05/2021", "IK2882918812", 3, 120.0, "Cancelled"));
        }
        orderAdapter.notifyDataSetChanged();
    }
}

