package com.example.fe.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.ui.auth.login.LoginActivity;
import com.example.fe.R;
import com.example.fe.ui.category.Category;
import com.example.fe.ui.category.CategoryActivity;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerRecommended, recyclerDeals;
    private RecommendedAdapter recommendedAdapter;
    private DealsAdapter dealsAdapter;

    // ✅ Thêm list category vào đây
    private final List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView imgCart = findViewById(R.id.imgCart);
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, com.example.fe.ui.cart.ShoppingCartActivity.class);
            startActivity(intent);
        });

        // 1) Khởi tạo dữ liệu Category trước
        initCategories();

        // 2) RecyclerViews
        recyclerRecommended = findViewById(R.id.recyclerRecommended);
        recyclerRecommended.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<ProductModel> recommendedList = buildRecommended(); // dùng getCategoryById()
        recommendedAdapter = new RecommendedAdapter(recommendedList);
        recyclerRecommended.setAdapter(recommendedAdapter);

        recyclerDeals = findViewById(R.id.recyclerDeals);
        recyclerDeals.setLayoutManager(new GridLayoutManager(this, 2));
        dealsAdapter = new DealsAdapter(buildDeals());
        recyclerDeals.setAdapter(dealsAdapter);

        // 3) BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_categories) {
                    startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
                } else if (id == R.id.nav_favourite) {
                    startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }
                return true;
            }
        });
    }

    // -------------------------
    // Data builders
    // -------------------------
    private void initCategories() {
        categoryList.clear();
        categoryList.add(new Category("Moisturizing Cream", "Hydrate and nourish your skin", "C01", R.drawable.img_moisturizer));
        categoryList.add(new Category("Lipstick", "Long-lasting and vibrant colors", "C02", R.drawable.img_lipstick_red));
        categoryList.add(new Category("Face Mask", "Revitalize your complexion", "C03", R.drawable.img_face_mask));
        categoryList.add(new Category("Sunscreen", "Protect your skin from UV rays", "C04", R.drawable.img_promo_2));
        categoryList.add(new Category("Perfume", "A scent that lasts all day", "C05", R.drawable.img_deal_3));
    }

    private List<ProductModel> buildRecommended() {
        List<ProductModel> list = new ArrayList<>();
        // use new constructor: (name, price, imageUrl, imageRes, category)
        list.add(new ProductModel("Son Môi Đỏ", "$15", null, R.drawable.img_lipstick_red, getCategoryById("C02")));
        list.add(new ProductModel("Kem Dưỡng Ẩm", "$20", null, R.drawable.img_moisturizer, getCategoryById("C01")));
        list.add(new ProductModel("Sữa Rửa Mặt", "$10", null, R.drawable.img_facewash, getCategoryById("C01")));
        list.add(new ProductModel("Mặt Nạ Dưỡng Da", "$12", null, R.drawable.img_face_mask, getCategoryById("C03")));
        return list;
    }

    private List<DealModel> buildDeals() {
        List<DealModel> list = new ArrayList<>();
        list.add(new DealModel("$325", "Orange Package I I\nI I bundle", R.drawable.img_deal_1));
        list.add(new DealModel("$89", "Green Tea Package 2\nI I bundle", R.drawable.img_deal_2));
        list.add(new DealModel("$125", "Vitamin C Package\nI I bundle", R.drawable.img_deal_3));
        list.add(new DealModel("$99", "Skincare Essentials\nI I bundle", R.drawable.img_deal_4));
        return list;
    }

    // -------------------------
    // Helpers
    // -------------------------
    private Category getCategoryById(String id) {
        if (id == null) return null;
        for (Category c : categoryList) {
            if (id.equalsIgnoreCase(c.getCategoryID())) {
                return c;
            }
        }
        return null; // không tìm thấy → nhớ handle null ở Adapter nếu cần
    }

    private void logoutUser() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
