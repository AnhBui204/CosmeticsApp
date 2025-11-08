package com.example.fe.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.data.AppData;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.home.ProductModel;
import com.example.fe.ui.home.RecommendedAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private RecyclerView recyclerProducts;

    // Chips
    private TextView chipPopular, chipLowPrice;

    // Data
    private final List<ProductModel> baseList = new ArrayList<>();
    private final List<ProductModel> currentList = new ArrayList<>();

    private RecommendedAdapter adapter;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_productlist);

        ImageView imgCart = findViewById(R.id.btnCart);
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, com.example.fe.ui.cart.ShoppingCartActivity.class);
            startActivity(intent);
        });

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Views
        tvGreeting = findViewById(R.id.tvGreeting);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Chips
        chipPopular  = findViewById(R.id.chipPopular);
        chipLowPrice = findViewById(R.id.chipLowPrice);

        // Category param
        categoryId = getIntent().getStringExtra("categoryId");

        // ✅ Set title từ AppData
        Category cat = AppData.getCategoryById(categoryId);
        tvGreeting.setText(cat != null ? cat.getName() : "Products");

        // ✅ Lấy data từ AppData thay vì tạo lại
        baseList.clear();
        baseList.addAll(AppData.getProductsByCategory(categoryId));

        currentList.clear();
        currentList.addAll(baseList);

        adapter = new RecommendedAdapter(currentList);
        recyclerProducts.setAdapter(adapter);

        // Handle chip click
        List<TextView> chips = Arrays.asList(chipPopular, chipLowPrice);
        setSelectedChip(chips, chipPopular);
        applyChipAction("Popular");

        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                TextView clicked = (TextView) v;
                setSelectedChip(chips, clicked);
                applyChipAction(clicked.getText().toString());
            });
        }
    }

    private void setSelectedChip(List<TextView> chips, TextView selected) {
        for (TextView c : chips) c.setSelected(c == selected);
    }

    private void applyChipAction(String label) {
        switch (label) {
            case "Popular":
                resetToBase();
                break;
            case "Low Price":
                sortByPriceAsc();
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void resetToBase() {
        currentList.clear();
        currentList.addAll(baseList);
    }

    private void sortByPriceAsc() {
        resetToBase();
        Collections.sort(currentList, Comparator.comparingInt(p -> parsePrice(p.getPrice())));
    }

    private int parsePrice(String priceStr) {
        if (priceStr == null) return Integer.MAX_VALUE;
        try {
            return Integer.parseInt(priceStr.replace("$","").replace(",","").trim());
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
