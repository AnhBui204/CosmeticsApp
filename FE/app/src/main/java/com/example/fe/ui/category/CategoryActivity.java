package com.example.fe.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerCategoryProducts;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerCategoryProducts = findViewById(R.id.recyclerCategoryProducts);
        recyclerCategoryProducts.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu giả
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Moisturizing Cream", "Hydrate and nourish your skin", "C01", R.drawable.img_moisturizer));
        categoryList.add(new Category("Lipstick", "Long-lasting and vibrant colors", "C02", R.drawable.img_lipstick_red));
        categoryList.add(new Category("Face Mask", "Revitalize your complexion", "C03", R.drawable.img_face_mask));
        categoryList.add(new Category("Sunscreen", "Protect your skin from UV rays", "C04", R.drawable.img_promo_2));
        categoryList.add(new Category("Perfume", "A scent that lasts all day", "C05", R.drawable.img_deal_3));

        adapter = new CategoryAdapter(categoryList);
        recyclerCategoryProducts.setAdapter(adapter);

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.getMenu().findItem(R.id.nav_categories).setChecked(true);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(CategoryActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_categories) {
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
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
