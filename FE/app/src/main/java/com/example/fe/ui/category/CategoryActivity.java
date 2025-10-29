package com.example.fe.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.LoginActivity;
import com.example.fe.R;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.home.HomeActivity;
import com.example.fe.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerCategoryProducts;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerCategoryProducts = findViewById(R.id.recyclerCategoryProducts);
        recyclerCategoryProducts.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu giả
        productList = new ArrayList<>();
        productList.add(new Product("Moisturizing Cream", "Hydrate and nourish your skin", "10 units", R.drawable.img_moisturizer));
        productList.add(new Product("Lipstick", "Long-lasting and vibrant colors", "5 units", R.drawable.img_lipstick_red));
        productList.add(new Product("Face Mask", "Revitalize your complexion", "20 units", R.drawable.img_face_mask));
        productList.add(new Product("Sunscreen", "Protect your skin from UV rays", "15 units", R.drawable.img_promo_2));
        productList.add(new Product("Perfume", "A scent that lasts all day", "8 units", R.drawable.img_deal_3));

        adapter = new ProductAdapter(productList);
        recyclerCategoryProducts.setAdapter(adapter);

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Đánh dấu tab Category là active
        bottomNav.getMenu().findItem(R.id.nav_categories).setChecked(true);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(CategoryActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_categories) {
                    // Đang ở Category
                    return true;
                } else if (id == R.id.nav_favourite) {
                    startActivity(new Intent(CategoryActivity.this, FavoriteActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

    }



    private void logoutUser() {
        // Ví dụ: quay về màn hình login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
