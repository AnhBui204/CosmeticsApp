package com.example.fe.ui.seller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.fe.utils.SessionManager;

// --- BIỂU ĐỒ ---
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
    // --- BIẾN MỚI TỪ FILE 2 ---
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
        sessionManager = new SessionManager(this); // Khởi tạo SessionManager

        // --- Ánh xạ UI Header (GỐC) ---
        headerLayout = findViewById(R.id.headerLayout);
        etHeaderSearch = findViewById(R.id.etHeaderSearch);
        headerOrderControls = findViewById(R.id.headerOrderControls);

        // --- ÁNH XẠ UI MỚI (TỪ FILE 2) ---
        productHeaderLayout = findViewById(R.id.productHeaderLayout);
        productBtnBack = findViewById(R.id.btnBack);
        productBtnRefresh = findViewById(R.id.btnRefresh);

        // --- LOGIC NÚT MỚI (TỪ FILE 2) ---
        // product header back: return to dashboard and hide product header
        productBtnBack.setOnClickListener(v -> {
            popToDashboard();
            showProductHeader(false);
            setHeaderExpanded(true);
        });

        // header refresh: try to find the products fragment and call its refresh
        productBtnRefresh.setOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.seller_fragment_container);
            if (f instanceof SellerProductsFragment) {
                ((SellerProductsFragment) f).refreshProducts();
            }
        });
        // ------------------------------------

        // Mặc định: expanded header with search visible
        setHeaderExpanded(true);

        // --- Ánh xạ UI Doanh thu (GỐC) ---
        lineChart = findViewById(R.id.lineChart);
        recyclerViewTopProducts = findViewById(R.id.recyclerView_topProducts);

        // --- Setup Bottom Navigation (ĐÃ HỢP NHẤT) ---
        BottomNavigationView bottomNav = findViewById(R.id.sellerBottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_seller_home) {
                // pop fragments to show default dashboard
                popToDashboard();
                setHeaderExpanded(true);
                showProductHeader(false); // Ẩn header sản phẩm
                return true;
            } else if (id == R.id.navigation_seller_orders) {
                replaceFragment(new SellerOrdersFragment());
                // for orders: show order controls in header and collapse header height
                setHeaderMode(true);
                showProductHeader(false); // Ẩn header sản phẩm
                return true;
            } else if (id == R.id.navigation_seller_products) {
                // show product list fragment
                replaceFragment(new SellerProductsFragment());
                // show product header and hide other header elements
                showProductHeader(true);
                return true;
            } else if (id == R.id.navigation_seller_account) {
                Intent intent = new Intent(SellerHomeActivity.this, ProfileActivity.class); // <-- Thay tên Activity cho đúng
                startActivity(intent);

                showProductHeader(false); // Ẩn header sản phẩm
                return true;
            }
            return false;
        });

        // --- Logic gọi API (GỐC) ---
        setupObservers(); // Bắt đầu lắng nghe
        loadDataFromApi(); // Kích hoạt gọi API
    }

    /**
     * Kích hoạt gọi API (GỐC)
     * Lấy sellerId từ SessionManager và truyền vào ViewModel
     */
    private void loadDataFromApi() {
        // 1. Lấy thông tin user (seller) từ session
        String sellerId = sessionManager.getUser().getId();

        if (sellerId != null && !sellerId.isEmpty()) {
            // 2. Truyền ID vào ViewModel để gọi CẢ HAI API
            viewModel.loadRevenueData(sellerId);
            viewModel.loadTopProducts(sellerId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người bán", Toast.LENGTH_LONG).show();
            // Cân nhắc chuyển về LoginActivity
        }
    }

    /**
     * Lắng nghe kết quả từ ViewModel (GỐC)
     */
    private void setupObservers() {
        // 1. Lắng nghe dữ liệu doanh thu
        viewModel.getRevenueData().observe(this, revenueList -> {
            if (revenueList != null && !revenueList.isEmpty()) {
                setupLineChart(revenueList);
            } else {
                Toast.makeText(this, "Không có dữ liệu doanh thu", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Lắng nghe dữ liệu top sản phẩm
        viewModel.getTopProducts().observe(this, productList -> {
            if (productList != null && !productList.isEmpty()) {
                setupTopProductsList(productList);
            } else {
                Toast.makeText(this, "Không có sản phẩm bán chạy", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Lắng nghe lỗi chung
        viewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Log.e("SellerHomeActivity", "Error: " + errorMessage);
                // Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hàm vẽ biểu đồ (GỐC)
     */
    private void setupLineChart(List<RevenueData> data) {
        if (lineChart == null) return;

        // 1. Tạo danh sách các Entry (điểm)
        ArrayList<Entry> entries = new ArrayList<>();
        // 1b. Tạo danh sách các nhãn (ngày) cho trục X
        final ArrayList<String> xLabels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            RevenueData item = data.get(i);
            entries.add(new Entry(i, (float) item.getRevenue()));

            // Lấy ngày, ví dụ "2025-11-12", chỉ lấy "11-12"
            try {
                // Thử chuyển đổi YYYY-MM-DD sang MM/dd
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(item.getDate());
                String shortDate = new SimpleDateFormat("MM/dd", Locale.US).format(date);
                xLabels.add(shortDate);
            } catch (Exception e) {
                xLabels.add(item.getDate()); // Nếu lỗi thì hiển thị ngày gốc
            }
        }

        // 2. Tạo DataSet
        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        int accentColor = ContextCompat.getColor(this, R.color.colorAccent);
        dataSet.setColor(accentColor);
        dataSet.setCircleColor(accentColor);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);

        // Hiển thị giá trị trên điểm (số nguyên, không có ,00)
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new DefaultValueFormatter(0));

        // 3. Tùy chỉnh màu fill (gradient)
        dataSet.setDrawFilled(true);
        Drawable fillDrawable = ContextCompat.getDrawable(this, R.drawable.chart_gradient);
        dataSet.setFillDrawable(fillDrawable);

        // 4. Gán data vào biểu đồ
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

        // 5. Tùy chỉnh lại biểu đồ
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true); // Bật chú thích

        // Tùy chỉnh trục X (Trục ngang - Ngày)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true); // Bật trục X
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đưa ngày xuống dưới
        xAxis.setGranularity(1f); // Đảm bảo hiển thị đủ các ngày
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
                // Hiển thị nhãn ngày
                int index = (int) value;
                if (index >= 0 && index < xLabels.size()) {
                    return xLabels.get(index);
                }
                return "";
            }
        });

        // Tùy chỉnh trục Y (Trục dọc - Tiền)
        lineChart.getAxisRight().setEnabled(false); // Tắt trục phải
        lineChart.getAxisLeft().setEnabled(true); // Bật trục trái
        lineChart.getAxisLeft().setAxisMinimum(0f); // Bắt đầu từ 0
        // Tự động thêm "k" (nghìn) hoặc "m" (triệu)
        lineChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());

        lineChart.invalidate(); // Refresh
    }

    /**
     * Hàm setup RecyclerView cho Top Products (GỐC)
     */
    private void setupTopProductsList(List<TopProductData> products) {
        if(recyclerViewTopProducts == null) return;

        // Sửa lỗi khi lồng RecyclerView trong ScrollView
        recyclerViewTopProducts.setNestedScrollingEnabled(false);

        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));

        // Đảm bảo bạn đã tạo file adapter này (ui/seller/adapter/TopProductAdapter.java)
        TopProductAdapter adapter = new TopProductAdapter(products);
        recyclerViewTopProducts.setAdapter(adapter);
    }


    // --- CÁC HÀM UI (ĐÃ THAY THẾ BẰNG LOGIC CỦA FILE 2) ---

    private void setHeaderExpanded(boolean expanded) {
        // delegate to setHeaderMode(false) for expanded, true for collapsed/orders
        setHeaderMode(!expanded);
    }

    private void setHeaderMode(boolean ordersMode) {
        // ordersMode=true => show headerOrderControls, hide etHeaderSearch, collapse header
        // ordersMode=false => show etHeaderSearch, hide headerOrderControls, expand header
        int heightDp = 160; // <<< Chiều cao đã thay đổi
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

    /**
     * HÀM MỚI TỪ FILE 2
     */
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
