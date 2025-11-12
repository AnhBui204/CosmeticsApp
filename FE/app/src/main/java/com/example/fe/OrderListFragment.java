package com.example.fe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.api.ApiClient;
import com.example.fe.api.UserService;
import com.example.fe.models.Order;
import com.example.fe.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "order_status";
    private String orderStatus;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private OrderAdapter orderAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_orders);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchOrdersFromAPI();

        return view;
    }

    private void fetchOrdersFromAPI() {
        progressBar.setVisibility(View.VISIBLE);

        SessionManager sessionManager = new SessionManager(requireContext());
        String userId = sessionManager.getUserId(); // nếu backend cần
        UserService userService = ApiClient.getAuthClient(requireContext()).create(UserService.class);


        Call<List<Order>> call;

        // Nếu backend tự lấy user từ token
        call = userService.getUserOrders();

        // Nếu backend cần userId trong URL (bỏ comment dòng dưới nếu dùng Cách 2)
        // call = userService.getUserOrders(userId);

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call,
                                   @NonNull Response<List<Order>> response) {
                if (!isAdded() || getView() == null) return; // fragment bị detach rồi

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();

                    if (orderStatus != null && !orderStatus.isEmpty()) {
                        orders.removeIf(o -> !orderStatus.equalsIgnoreCase(o.getStatus()));
                    }

                    orderAdapter = new OrderAdapter(orders, requireContext());
                    recyclerView.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                // Log lỗi chi tiết
                Log.e("OrderListFragment", "Lỗi khi tải đơn hàng", t);
                Toast.makeText(getContext(), "Lỗi khi tải đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
