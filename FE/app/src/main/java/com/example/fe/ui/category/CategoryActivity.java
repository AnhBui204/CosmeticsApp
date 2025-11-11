package com.example.fe.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.ui.auth.login.LoginActivity;
import com.example.fe.ui.cart.ShoppingCartActivity;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.home.HomeActivity;
import com.example.fe.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerCategoryProducts;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Cart icon click
        ImageView imgCart = findViewById(R.id.imgCart);
        imgCart.setOnClickListener(v -> startActivity(new Intent(CategoryActivity.this, ShoppingCartActivity.class)));

        // RecyclerView setup
        recyclerCategoryProducts = findViewById(R.id.recyclerCategoryProducts);
        recyclerCategoryProducts.setLayoutManager(new LinearLayoutManager(this));

        // Load categories từ BE và map sang FE model
        loadCategoriesFromApi();

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.getMenu().findItem(R.id.nav_categories).setChecked(true);

        bottomNav.setOnItemSelectedListener(item -> {
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
        });
    }

    private void loadCategoriesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<com.example.fe.models.Category>> call = apiService.getCategories(); // BE model

        call.enqueue(new Callback<List<com.example.fe.models.Category>>() {
            @Override
            public void onResponse(Call<List<com.example.fe.models.Category>> call, Response<List<com.example.fe.models.Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.fe.models.Category> categoryListBE = response.body();

                    // Map sang FE Category
                    List<Category> categoryListFE = new ArrayList<>();
                    for (com.example.fe.models.Category c : categoryListBE) {
                        categoryListFE.add(new Category(
                                c.getName(),
                                c.getDescription() != null ? c.getDescription() : "No description available",
                                c.getId(), // id
                                R.drawable.img_promo_2 // FE model cần image resource
                        ));
                    }

                    adapter = new CategoryAdapter(categoryListFE);
                    recyclerCategoryProducts.setAdapter(adapter);
                } else {
                    Toast.makeText(CategoryActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<com.example.fe.models.Category>> call, Throwable t) {
                Toast.makeText(CategoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logoutUser() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
