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

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fe.R;
import com.example.fe.data.RevenueData; // Model
import com.example.fe.data.TopProductData; // Model
import com.example.fe.ui.seller.adapter.TopProductAdapter; // Adapter
import com.example.fe.utils.SessionManager; // Import để lấy ID

// --- IMPORT THÊM CHO BIỂU ĐỒ ---
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

        // --- Ánh xạ UI Header ---
        headerLayout = findViewById(R.id.headerLayout);
        etHeaderSearch = findViewById(R.id.etHeaderSearch);
        headerOrderControls = findViewById(R.id.headerOrderControls);
        setHeaderExpanded(true); // Mặc định

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
                return true;
            } else if (id == R.id.navigation_seller_orders) {
                replaceFragment(new SellerOrdersFragment());
                setHeaderMode(true);
                return true;
            } else if (id == R.id.navigation_seller_products) {
                startActivity(new android.content.Intent(SellerHomeActivity.this, SellerProductListActivity.class));
                setHeaderMode(false);
                return true;
            } else if (id == R.id.navigation_seller_account) {
                // (Chưa làm)
                return true;
            }
            return false;
        });

        // --- Logic gọi API ---
        setupObservers(); // Bắt đầu lắng nghe
        loadDataFromApi(); // Kích hoạt gọi API
    }

    /**
     * Kích hoạt gọi API
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
     * Lắng nghe kết quả từ ViewModel
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
     * Hàm vẽ biểu đồ (ĐÃ CẬP NHẬT)
     * - Hiển thị trục X (ngày), trục Y (tiền)
     * - Hiển thị giá trị trên điểm
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

        // --- SỬA --- Hiển thị giá trị trên điểm (số nguyên, không có ,00)
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

        // 5. --- SỬA --- Tùy chỉnh lại biểu đồ
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
     * Hàm setup RecyclerView cho Top Products (ĐÃ SỬA)
     * - Thêm setNestedScrollingEnabled(false)
     */
    private void setupTopProductsList(List<TopProductData> products) {
        if(recyclerViewTopProducts == null) return;

        // --- SỬA ---
        // Sửa lỗi khi lồng RecyclerView trong ScrollView
        recyclerViewTopProducts.setNestedScrollingEnabled(false);
        // -----------

        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));

        // Đảm bảo bạn đã tạo file adapter này (ui/seller/adapter/TopProductAdapter.java)
        TopProductAdapter adapter = new TopProductAdapter(products);
        recyclerViewTopProducts.setAdapter(adapter);
    }


    // --- Các hàm UI (giữ nguyên) ---

    private void setHeaderExpanded(boolean expanded) {
        setHeaderMode(!expanded);
    }

    private void setHeaderMode(boolean ordersMode) {
        int heightDp = 160;
        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
        lp.height = heightPx;
        headerLayout.setLayoutParams(lp);
        etHeaderSearch.setVisibility(ordersMode ? View.GONE : View.VISIBLE);
        if (headerOrderControls != null) headerOrderControls.setVisibility(ordersMode ? View.VISIBLE : View.GONE);
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