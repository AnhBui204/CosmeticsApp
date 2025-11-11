package com.example.fe.ui.seller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.util.TypedValue;
import com.example.fe.ui.voucher.VoucherFragment;
import com.example.fe.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerHomeActivity extends AppCompatActivity {
    private RelativeLayout headerLayout;
    private EditText etHeaderSearch;
    private View headerOrderControls;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.seller_homepage);

        headerLayout = findViewById(R.id.headerLayout);
        etHeaderSearch = findViewById(R.id.etHeaderSearch);
        headerOrderControls = findViewById(R.id.headerOrderControls);

        // default: expanded header with search visible
        setHeaderExpanded(true);

         BottomNavigationView bottomNav = findViewById(R.id.sellerBottomNav);
         bottomNav.setOnItemSelectedListener(item -> {
             int id = item.getItemId();

             if (id == R.id.navigation_seller_home) {
                 // Quay lại màn hình dashboard mặc định
                 popToDashboard();
                 setHeaderExpanded(true);
                 return true;

             } else if (id == R.id.navigation_seller_orders) {
                 replaceFragment(new SellerOrdersFragment());
                 // Hiển thị header rút gọn khi ở màn orders
                 setHeaderMode(true);
                 return true;
//
//             } else if (id == R.id.navigation_seller_products) {
//                 replaceFragment(new SellerProductsFragment());
//                 setHeaderMode(false);
//                 return true;

             } else if (id == R.id.navigation_seller_vouchers) {
                 replaceFragment(new VoucherFragment());
                 setHeaderMode(false); // nếu bạn muốn ẩn header khi vào phần voucher
                 return true;

//             } else if (id == R.id.navigation_seller_account) {
//                 replaceFragment(new SellerAccountFragment());
//                 setHeaderMode(false);
//                 return true;
             }

             return false;
         });

     }

    private void setHeaderExpanded(boolean expanded) {
        // delegate to setHeaderMode(false) for expanded, true for collapsed/orders
        setHeaderMode(!expanded);
    }

    private void setHeaderMode(boolean ordersMode) {
        // ordersMode=true => show headerOrderControls, hide etHeaderSearch, collapse header
        // ordersMode=false => show etHeaderSearch, hide headerOrderControls, expand header
        int heightDp = ordersMode ? 160 : 260;
        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
        lp.height = heightPx;
        headerLayout.setLayoutParams(lp);
        etHeaderSearch.setVisibility(ordersMode ? View.GONE : View.VISIBLE);
        if (headerOrderControls != null) headerOrderControls.setVisibility(ordersMode ? View.VISIBLE : View.GONE);
    }

     private void popToDashboard() {
         // clear back stack and show the XML default content
         getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
         // If there is any fragment occupying the container, remove it
         Fragment f = getSupportFragmentManager().findFragmentById(R.id.seller_fragment_container);
         if (f != null) {
             FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
             ft.remove(f);
             ft.commitAllowingStateLoss();
         }
     }

    private void replaceFragment(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.seller_fragment_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

}
