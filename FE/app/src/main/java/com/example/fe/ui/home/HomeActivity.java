package com.example.fe.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.LoginActivity;
import com.example.fe.R;
import com.example.fe.ui.category.CategoryActivity;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerRecommended, recyclerDeals;
    private RecommendedAdapter recommendedAdapter;
    private DealsAdapter dealsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // RecyclerViews setup
        recyclerRecommended = findViewById(R.id.recyclerRecommended);
        recyclerRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<ProductModel> recommendedList = new ArrayList<>();
        recommendedList.add(new ProductModel("Son Môi Đỏ", "Thương hiệu A", "$15", R.drawable.img_lipstick_red));
        recommendedList.add(new ProductModel("Kem Dưỡng Ẩm", "Thương hiệu B", "$20", R.drawable.img_moisturizer));
        recommendedList.add(new ProductModel("Sữa Rửa Mặt", "Thương hiệu C", "$10", R.drawable.img_facewash));
        recommendedList.add(new ProductModel("Mặt Nạ Dưỡng Da", "Thương hiệu D", "$12", R.drawable.img_face_mask));

        recommendedAdapter = new RecommendedAdapter(recommendedList);
        recyclerRecommended.setAdapter(recommendedAdapter);

        recyclerDeals = findViewById(R.id.recyclerDeals);
        recyclerDeals.setLayoutManager(new GridLayoutManager(this, 2));

        List<DealModel> dealsList = new ArrayList<>();
        dealsList.add(new DealModel("$325", "Orange Package I I\nI I bundle", R.drawable.img_deal_1));
        dealsList.add(new DealModel("$89", "Green Tea Package 2\nI I bundle", R.drawable.img_deal_2));
        dealsList.add(new DealModel("$125", "Vitamin C Package\nI I bundle", R.drawable.img_deal_3));
        dealsList.add(new DealModel("$99", "Skincare Essentials\nI I bundle", R.drawable.img_deal_4));

        dealsAdapter = new DealsAdapter(dealsList);
        recyclerDeals.setAdapter(dealsAdapter);

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Đánh dấu tab Home là active
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Đang ở Home, không làm gì
                } else if (id == R.id.nav_categories) {
                    startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
                } else if (id == R.id.nav_favourite) {
                    startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }

                return true; // trả về true để highlight icon
            }
        });

    }


    private void logoutUser() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
