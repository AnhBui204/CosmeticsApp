package com.example.fe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fe.models.OrderViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyOrdersFragment extends Fragment {

    private OrderViewModel viewModel;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private OrdersPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Bạn cần file layout R.layout.fragment_my_orders
        // (Đây là file XML bạn cung cấp trong prompt trước, đã sửa lỗi)
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        // Khởi tạo Views
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        // (Setup toolbar nếu cần, ví dụ: ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar))

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Adapter
        pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Liên kết Tab và ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Pending");
                    break;
                case 1:
                    tab.setText("Delivered");
                    break;
                case 2:
                    tab.setText("Cancelled");
                    break;
            }
        }).attach();

        // Khởi tạo ViewModel
        // Dùng requireActivity() để ViewModel này được chia sẻ
        // giữa MyOrdersFragment và các OrderListFragment con
        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        // Lắng nghe lỗi
        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // GỌI API MỘT LẦN DUY NHẤT TẠI ĐÂY
        viewModel.fetchMyOrders();
    }
}