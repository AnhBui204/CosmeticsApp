package com.example.fe.ui.seller;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class RevenueActivity extends AppCompatActivity {

    private LineChart lineChart;
    private RecyclerView recyclerViewTopProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        // Ánh xạ View
        lineChart = findViewById(R.id.lineChart);
        recyclerViewTopProducts = findViewById(R.id.recyclerView_topProducts);

        // Setup
        setupLineChart();
        setupRecyclerView();
    }

    private void setupLineChart() {
        // 1. Tạo dữ liệu mẫu (dummy data)
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 100));
        entries.add(new Entry(2, 120));
        entries.add(new Entry(3, 110));
        entries.add(new Entry(4, 150));
        entries.add(new Entry(5, 180));
        entries.add(new Entry(6, 160));
        entries.add(new Entry(7, 200));

        // 2. Tạo DataSet và tùy chỉnh màu sắc
        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        int accentColor = ContextCompat.getColor(this, R.color.colorAccent);
        dataSet.setColor(accentColor); // Màu đường line
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(accentColor); // Màu chấm
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false); // Ẩn giá trị trên chấm

        // 3. Tùy chỉnh màu fill (gradient)
        dataSet.setDrawFilled(true);
        Drawable fillDrawable = ContextCompat.getDrawable(this, R.drawable.chart_gradient); // Tạo file drawable gradient
        dataSet.setFillDrawable(fillDrawable);

        // 4. Gán data vào biểu đồ
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

        // 5. Tùy chỉnh biểu đồ
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate(); // Refresh
    }

    private void setupRecyclerView() {
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));
        
        // Tạo dữ liệu mẫu (dummy data)
        List<Object> topProducts = new ArrayList<>(); // Thay Object bằng Model TopProduct của bạn
        topProducts.add(new Object()); // Thêm 3 item mẫu
        topProducts.add(new Object());
        topProducts.add(new Object());
        
        // Cần tạo Adapter (ví dụ: TopProductAdapter)
        // TopProductAdapter adapter = new TopProductAdapter(topProducts);
        // recyclerViewTopProducts.setAdapter(adapter);
        
        // Tạm thời, bạn có thể comment dòng setAdapter cho đến khi tạo Adapter
    }

}