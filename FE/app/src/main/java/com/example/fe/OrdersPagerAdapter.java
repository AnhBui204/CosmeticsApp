package com.example.fe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 3;

    public OrdersPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về fragment tương ứng cho mỗi tab
        // Chúng ta truyền loại đơn hàng (status) cho mỗi fragment
        switch (position) {
            case 0:
                return OrderListFragment.newInstance("Pending");
            case 1:
                // Show delivered orders in the middle tab
                return OrderListFragment.newInstance("Delivered");
            case 2:
                return OrderListFragment.newInstance("Cancelled");
            default:
                // Không nên trả về null; trả về Pending như mặc định
                return OrderListFragment.newInstance("Pending");
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
