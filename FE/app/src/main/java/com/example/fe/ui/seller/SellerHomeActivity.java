package com.example.fe.ui.seller;

// Import các thư viện cần thiết
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fe.R;
import com.example.fe.data.RevenueData;
import com.example.fe.data.TopProductData;
import com.example.fe.ui.profile.ProfileActivity;
import com.example.fe.ui.seller.adapter.TopProductAdapter;
import com.example.fe.ui.seller.fragment.SellerOrdersFragment;
import com.example.fe.ui.seller.fragment.SellerProductsFragment;
import com.example.fe.ui.voucher.VoucherFragment;
import com.example.fe.utils.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SellerHomeActivity extends AppCompatActivity {

    // --- Biến UI Header ---
    private RelativeLayout headerLayout;
    private EditText etHeaderSearch;
    private View headerOrderControls;

    // --- Biến mới cho Product Header ---
    private View productHeaderLayout;
    private ImageButton productBtnBack;
    private ImageButton productBtnRefresh;

    // --- Biến UI Doanh thu ---
    private LineChart lineChart;
    private RecyclerView recyclerViewTopProducts;

    // --- Biến cho API ---
    private SellerViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_homepage);

        // --- Khởi tạo ViewModel và SessionManager ---
        viewModel = new ViewModelProvider(this).get(SellerViewModel.class);
        sessionManager = new SessionManager(this);

        // --- Ánh xạ UI Header ---
        headerLayout = findViewById(R.id.headerLayout);
        etHeaderSearch = findViewById(R.id.etHeaderSearch);
        headerOrderControls = findViewById(R.id.headerOrderControls);

        // --- Ánh xạ UI Product Header ---
        productHeaderLayout = findViewById(R.id.productHeaderLayout);
        productBtnBack = findViewById(R.id.btnBack);
        productBtnRefresh = findViewById(R.id.btnRefresh);

        // --- Logic Product Header ---
        productBtnBack.setOnClickListener(v -> {
            popToDashboard();
            showProductHeader(false);
            setHeaderExpanded(true);
        });

        productBtnRefresh.setOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.seller_fragment_container);
            if (f instanceof SellerProductsFragment) {
                ((SellerProductsFragment) f).refreshProducts();
            }
        });

        // --- Ánh xạ UI Doanh thu ---
        lineChart = findViewById(R.id.lineChart);
        recyclerViewTopProducts = findViewById(R.id.recyclerView_topProducts);

        // --- Setup Bottom Navigation ---
        BottomNavigationView bottomNav = findViewById(R.id.sellerBottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_seller_home) {
                popToDashboard();
                setHeaderExpanded(true);
                showProductHeader(false);
                return true;
            } else if (id == R.id.navigation_seller_orders) {
                replaceFragment(new SellerOrdersFragment());
                setHeaderMode(true);
                showProductHeader(false);
                return true;
            } else if (id == R.id.navigation_seller_products) {
                replaceFragment(new SellerProductsFragment());
                showProductHeader(true);
                return true;
            } else if (id == R.id.navigation_seller_vouchers) {
                replaceFragment(new VoucherFragment());
                showProductHeader(false);
                setHeaderMode(false);
                return true;
            } else if (id == R.id.navigation_seller_account) {
                Intent intent = new Intent(SellerHomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                showProductHeader(false);
                return true;
            }
            return false;
        });

        // --- Gọi API ---
        setupObservers();
        loadDataFromApi();
    }

    // --- API ---
    private void loadDataFromApi() {
        String sellerId = sessionManager.getUser().getId();
        if (sellerId != null && !sellerId.isEmpty()) {
            viewModel.loadRevenueData(sellerId);
            viewModel.loadTopProducts(sellerId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người bán", Toast.LENGTH_LONG).show();
        }
    }

    private void setupObservers() {
        viewModel.getRevenueData().observe(this, revenueList -> {
            if (revenueList != null && !revenueList.isEmpty()) {
                setupLineChart(revenueList);
            } else {
                Toast.makeText(this, "Không có dữ liệu doanh thu", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getTopProducts().observe(this, productList -> {
            if (productList != null && !productList.isEmpty()) {
                setupTopProductsList(productList);
            } else {
                Toast.makeText(this, "Không có sản phẩm bán chạy", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Log.e("SellerHomeActivity", "Error: " + errorMessage);
            }
        });
    }

    // --- Biểu đồ ---
    private void setupLineChart(List<RevenueData> data) {
        if (lineChart == null) return;
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            RevenueData item = data.get(i);
            entries.add(new Entry(i, (float) item.getRevenue()));
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(item.getDate());
                String shortDate = new SimpleDateFormat("MM/dd", Locale.US).format(date);
                xLabels.add(shortDate);
            } catch (Exception e) {
                xLabels.add(item.getDate());
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        int accentColor = ContextCompat.getColor(this, R.color.colorAccent);
        dataSet.setColor(accentColor);
        dataSet.setCircleColor(accentColor);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new DefaultValueFormatter(0));
        dataSet.setDrawFilled(true);
        Drawable fillDrawable = ContextCompat.getDrawable(this, R.drawable.chart_gradient);
        dataSet.setFillDrawable(fillDrawable);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
                int index = (int) value;
                return (index >= 0 && index < xLabels.size()) ? xLabels.get(index) : "";
            }
        });

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());
        lineChart.invalidate();
    }

    // --- Top Products ---
    private void setupTopProductsList(List<TopProductData> products) {
        if (recyclerViewTopProducts == null) return;
        recyclerViewTopProducts.setNestedScrollingEnabled(false);
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));
        TopProductAdapter adapter = new TopProductAdapter(products);
        recyclerViewTopProducts.setAdapter(adapter);
    }

    // --- UI ---
    private void setHeaderExpanded(boolean expanded) {
        setHeaderMode(!expanded);
    }

    private void setHeaderMode(boolean ordersMode) {
        int heightDp = 160;
        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
        lp.height = heightPx;
        headerLayout.setLayoutParams(lp);
        etHeaderSearch.setVisibility(ordersMode ? View.GONE : View.VISIBLE);
        if (headerOrderControls != null)
            headerOrderControls.setVisibility(ordersMode ? View.VISIBLE : View.GONE);
        if (productHeaderLayout != null && productHeaderLayout.getVisibility() == View.VISIBLE) {
            productHeaderLayout.setVisibility(ordersMode ? View.GONE : View.VISIBLE);
        }
    }

    private void showProductHeader(boolean show) {
        if (productHeaderLayout == null) return;
        productHeaderLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            if (etHeaderSearch != null) etHeaderSearch.setVisibility(View.GONE);
            if (headerOrderControls != null) headerOrderControls.setVisibility(View.GONE);
            int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
            lp.height = heightPx;
            headerLayout.setLayoutParams(lp);
        } else {
            setHeaderExpanded(true);
        }
    }

    private void popToDashboard() {
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
