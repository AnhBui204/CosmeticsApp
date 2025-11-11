package com.example.fe.ui.seller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.util.TypedValue;

import com.example.fe.R;
import com.example.fe.ui.seller.fragment.SellerOrdersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerHomeActivity extends AppCompatActivity {
    private RelativeLayout headerLayout;
    private EditText etHeaderSearch;
    private View headerOrderControls;
    private View productHeaderLayout;
    private ImageButton productBtnBack;
    private ImageButton productBtnRefresh;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.seller_homepage);

        headerLayout = findViewById(R.id.headerLayout);
        etHeaderSearch = findViewById(R.id.etHeaderSearch);
        headerOrderControls = findViewById(R.id.headerOrderControls);
        productHeaderLayout = findViewById(R.id.productHeaderLayout);
        productBtnBack = findViewById(R.id.btnBack);
        productBtnRefresh = findViewById(R.id.btnRefresh);

        // product header back: return to dashboard and hide product header
        productBtnBack.setOnClickListener(v -> {
            popToDashboard();
            showProductHeader(false);
            setHeaderExpanded(true);
        });

        // header refresh: try to find the products fragment and call its refresh
        productBtnRefresh.setOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.seller_fragment_container);
            if (f instanceof com.example.fe.ui.seller.fragment.SellerProductsFragment) {
                ((com.example.fe.ui.seller.fragment.SellerProductsFragment) f).refreshProducts();
            }
        });

        // default: expanded header with search visible
        setHeaderExpanded(true);

         BottomNavigationView bottomNav = findViewById(R.id.sellerBottomNav);
         bottomNav.setOnItemSelectedListener(item -> {
             int id = item.getItemId();
             if (id == R.id.navigation_seller_home) {
                 // pop fragments to show default dashboard (we keep the layout's default content)
                 popToDashboard();
                setHeaderExpanded(true);
                 return true;
             } else if (id == R.id.navigation_seller_orders) {
                 replaceFragment(new SellerOrdersFragment());
                // for orders: show order controls in header and collapse header height
                setHeaderMode(true);
                 return true;
            } else if (id == R.id.navigation_seller_products) {
                // show product list fragment inside the seller homepage container
                replaceFragment(new com.example.fe.ui.seller.fragment.SellerProductsFragment());
                // show product header and hide other header elements
                showProductHeader(true);
                return true;
             } else if (id == R.id.navigation_seller_account) {
//                 replaceFragment(new PlaceholderSellerFragment("Account"));
//                setHeaderMode(false);
                 return true;
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
        // ensure product header hidden when toggling modes unless explicitly shown
        if (productHeaderLayout != null && productHeaderLayout.getVisibility() == View.VISIBLE) {
            // if currently visible, keep it visible only when not in orders mode
            productHeaderLayout.setVisibility(ordersMode ? View.GONE : View.VISIBLE);
        }
    }

    private void showProductHeader(boolean show) {
        if (productHeaderLayout == null) return;
        productHeaderLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        // when showing product header, hide search and order controls
        if (show) {
            if (etHeaderSearch != null) etHeaderSearch.setVisibility(View.GONE);
            if (headerOrderControls != null) headerOrderControls.setVisibility(View.GONE);
            // reduce header height a bit to match product header style
            int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
            lp.height = heightPx;
            headerLayout.setLayoutParams(lp);
        } else {
            // restore expanded header
            setHeaderExpanded(true);
        }
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
